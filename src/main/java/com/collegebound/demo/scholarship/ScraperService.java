package com.collegebound.demo.scholarship;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ScraperService {

    private static final String BASE_URL = "https://dallascollege.academicworks.com/opportunities";
    private static final String SITE_ROOT = "https://dallascollege.academicworks.com";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_6) AppleWebKit/537.36 "
                    + "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

    public List<Scholarship> scrape() {
        List<String> pageUrls = getPageUrls();
        if (pageUrls.isEmpty()) {
            return Collections.emptyList();
        }

        ExecutorService pageExecutor = Executors.newFixedThreadPool(Math.min(pageUrls.size(), 8));
        try {
            List<CompletableFuture<List<Scholarship>>> pageFutures = pageUrls.stream()
                    .map(pageUrl -> CompletableFuture.supplyAsync(() -> scrapePage(pageUrl), pageExecutor))
                    .toList();

            return pageFutures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } finally {
            shutdownExecutor(pageExecutor);
        }
    }

    private List<String> getPageUrls() {
        try {
            Document firstPage = getDoc(BASE_URL);
            Elements options = firstPage.select("option[name=page]");
            if (options.isEmpty()) {
                return List.of(BASE_URL);
            }

            List<String> pages = new ArrayList<>();
            for (Element option : options) {
                String directUrl = option.attr("data-direct-url").trim();
                if (directUrl.isEmpty()) {
                    continue;
                }
                pages.add(toAbsoluteUrl(directUrl));
            }
            return pages.isEmpty() ? List.of(BASE_URL) : pages;
        } catch (IOException e) {
            System.err.println("Unable to fetch pagination pages: " + e.getMessage());
            return List.of(BASE_URL);
        }
    }

    private List<Scholarship> scrapePage(String pageUrl) {
        try {
            Document pageDoc = getDoc(pageUrl);
            Elements rows = pageDoc.select("table tr");
            if (rows.size() <= 1) {
                return Collections.emptyList();
            }

            List<ScholarshipSeed> seeds = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                ScholarshipSeed seed = parseRow(rows.get(i));
                if (seed != null) {
                    seeds.add(seed);
                }
            }

            if (seeds.isEmpty()) {
                return Collections.emptyList();
            }

            ExecutorService detailExecutor = Executors.newFixedThreadPool(Math.min(seeds.size(), 12));
            try {
                List<CompletableFuture<Scholarship>> details = seeds.stream()
                        .map(seed -> CompletableFuture.supplyAsync(() -> new Scholarship(
                                seed.name(),
                                seed.deadline(),
                                seed.award(),
                                seed.link(),
                                scrapeQuestions(seed.link())
                        ), detailExecutor))
                        .toList();

                return details.stream().map(CompletableFuture::join).collect(Collectors.toList());
            } finally {
                shutdownExecutor(detailExecutor);
            }
        } catch (IOException e) {
            System.err.println("Failed to scrape page " + pageUrl + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private ScholarshipSeed parseRow(Element row) {
        Elements cells = row.select("td");
        if (cells.size() < 2) {
            return null;
        }

        String award = cells.get(0).text().trim();
        String deadline = cells.get(1).text().trim();
        if (deadline.isBlank() || "Ended".equalsIgnoreCase(deadline)) {
            return null;
        }

        Element linkTag = row.selectFirst("a");
        if (linkTag == null) {
            return null;
        }

        String name = linkTag.text().trim();
        String href = linkTag.attr("href").trim();
        if (name.isBlank() || href.isBlank()) {
            return null;
        }

        String fullLink = toAbsoluteUrl(href.replace("opportunities/", ""));
        return new ScholarshipSeed(name, deadline, award, fullLink);
    }

    private List<String> scrapeQuestions(String url) {
        List<String> qs = new ArrayList<>();
        try {
            Document doc = getDoc(url);
            Elements questionElements = doc.select(".js-question");
            for (Element q : questionElements) {
                String text = q.text().trim();
                if (!text.isBlank()) {
                    qs.add(text);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not reach detail page " + url + ": " + e.getMessage());
        }
        return qs;
    }

    private Document getDoc(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.9")
                .timeout(15000)
                .get();
    }

    private String toAbsoluteUrl(String pathOrUrl) {
        if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
            return pathOrUrl;
        }

        if (pathOrUrl.startsWith("/")) {
            return SITE_ROOT + pathOrUrl;
        }

        return BASE_URL + (pathOrUrl.startsWith("?") ? pathOrUrl : "/" + pathOrUrl);
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private record ScholarshipSeed(String name, String deadline, String award, String link) {}
}