package com.collegebound.demo.college;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CollegeScorecardClient {

    private static final String BASE_URL = "https://api.data.gov/ed/collegescorecard/v1/schools";
        private static final String FIELDS = String.join(",",
            "id",
            "school.name",
            "school.state",
            "school.ownership",
            "latest.admissions.admission_rate.overall",
            "latest.cost.avg_net_price.overall",
            "latest.student.retention_rate.four_year.full_time_pooled",
            "latest.admissions.sat_scores.average.overall",
            "latest.admissions.act_scores.midpoint.cumulative",
            "latest.student.size",
            "latest.earnings.6_yrs_after_entry.median",
            "school.school_url",
            "latest.student.demographics.first_generation",
            "latest.student.part_time_share",
            "latest.student.demographics.men",
            "latest.student.demographics.women",
            "latest.student.demographics.race_ethnicity.white",
            "latest.student.demographics.race_ethnicity.black",
            "latest.student.demographics.race_ethnicity.hispanic",
            "latest.student.demographics.race_ethnicity.asian",
            "latest.student.demographics.race_ethnicity.two_or_more",
            "latest.student.demographics.race_ethnicity.unknown");
    private static final Duration CACHE_TTL = Duration.ofHours(6);

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Value("${college.scorecard.api-key:}")
    private String apiKey;

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public Optional<SearchResult> search(String q, String state, int limit) {
        if (!isConfigured()) {
            return Optional.empty();
        }

        Map<String, String> params = Map.ofEntries(
                Map.entry("api_key", apiKey),
                Map.entry("fields", FIELDS),
                Map.entry("school.operating", "1"),
                Map.entry("sort", "school.name"),
                Map.entry("per_page", String.valueOf(Math.max(1, Math.min(limit, 100)))));

        StringBuilder url = new StringBuilder(BASE_URL).append("?").append(toQueryString(params));

        if (q != null && !q.isBlank()) {
            url.append("&school.name=").append(encode(q.trim()));
        }

        if (state != null && !state.isBlank()) {
            url.append("&school.state=").append(encode(state.trim().toUpperCase(Locale.ROOT)));
        }

        return fetchColleges(url.toString()).map(result -> new SearchResult(
                result.colleges,
                "U.S. Department of Education College Scorecard API",
                result.fetchedAt,
                result.fromCache));
    }

    public Optional<College> findById(String id) {
        if (!isConfigured() || id == null || id.isBlank()) {
            return Optional.empty();
        }

        String url = BASE_URL + "?" + toQueryString(Map.of(
                "api_key", apiKey,
                "fields", FIELDS,
                "id", id.trim(),
                "per_page", "1"));

        return fetchColleges(url).flatMap(result -> result.colleges.stream().findFirst());
    }

    private Optional<FetchResult> fetchColleges(String url) {
        CacheEntry cached = cache.get(url);
        Instant now = Instant.now();
        if (cached != null && cached.expiresAt.isAfter(now)) {
            return Optional.of(new FetchResult(cached.colleges, cached.fetchedAt, true));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return Optional.empty();
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode results = root.path("results");

            if (!results.isArray()) {
                return Optional.of(new FetchResult(List.of(), formatInstant(now), false));
            }

            List<College> colleges = new ArrayList<>();
            for (JsonNode node : results) {
                College college = toCollege(node);
                if (college != null) {
                    colleges.add(college);
                }
            }

            String fetchedAt = formatInstant(now);
            cache.put(url, new CacheEntry(colleges, fetchedAt, now.plus(CACHE_TTL)));
            return Optional.of(new FetchResult(colleges, fetchedAt, false));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (IOException | RuntimeException e) {
            return Optional.empty();
        }
    }

    private College toCollege(JsonNode node) {
        String id = text(node, "id");
        String name = text(node, "school.name");

        if (id == null || name == null || name.isBlank()) {
            return null;
        }

        return new College(
            id,
            name,
            text(node, "school.state"),
            ownershipToType(integer(node, "school.ownership")),
            decimal(node, "latest.admissions.admission_rate.overall"),
            integer(node, "latest.cost.avg_net_price.overall"),
            decimal(node, "latest.student.retention_rate.four_year.full_time_pooled"),
            integer(node, "latest.admissions.sat_scores.average.overall"),
            integer(node, "latest.admissions.act_scores.midpoint.cumulative"),
            integer(node, "latest.student.size"),
                null,
            integer(node, "latest.earnings.6_yrs_after_entry.median"),
            text(node, "school.school_url"),
                null,
            decimal(node, "latest.student.demographics.first_generation"),
            decimal(node, "latest.student.part_time_share"),
            decimal(node, "latest.student.demographics.men"),
            decimal(node, "latest.student.demographics.women"),
            decimal(node, "latest.student.demographics.race_ethnicity.white"),
            decimal(node, "latest.student.demographics.race_ethnicity.black"),
            decimal(node, "latest.student.demographics.race_ethnicity.hispanic"),
            decimal(node, "latest.student.demographics.race_ethnicity.asian"),
            decimal(node, "latest.student.demographics.race_ethnicity.two_or_more"),
            decimal(node, "latest.student.demographics.race_ethnicity.unknown"));
    }

    private String ownershipToType(Integer ownership) {
        if (ownership == null) {
            return "Unknown";
        }

        if (ownership == 1) {
            return "Public";
        }

        if (ownership == 2 || ownership == 3) {
            return "Private";
        }

        return "Unknown";
    }

    private String text(JsonNode node, String path) {
        JsonNode value = nodeAt(node, path);
        if (value.isMissingNode() || value.isNull()) {
            return null;
        }
        return value.asText(null);
    }

    private Integer integer(JsonNode node, String path) {
        JsonNode value = nodeAt(node, path);
        if (value.isMissingNode() || value.isNull()) {
            return null;
        }
        if (!value.isNumber()) {
            return null;
        }
        return value.asInt();
    }

    private Double decimal(JsonNode node, String path) {
        JsonNode value = nodeAt(node, path);
        if (value.isMissingNode() || value.isNull()) {
            return null;
        }
        if (!value.isNumber()) {
            return null;
        }
        return value.asDouble();
    }

    private JsonNode nodeAt(JsonNode node, String path) {
        if (node.has(path)) {
            return node.path(path);
        }

        JsonNode current = node;
        for (String segment : path.split("\\.")) {
            current = current.path(segment);
            if (current.isMissingNode()) {
                break;
            }
        }

        return current;
    }

    private String toQueryString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String formatInstant(Instant instant) {
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }

    public static class SearchResult {
        private final List<College> colleges;
        private final String source;
        private final String dataLastRefreshed;
        private final boolean cacheHit;

        public SearchResult(List<College> colleges, String source, String dataLastRefreshed, boolean cacheHit) {
            this.colleges = colleges;
            this.source = source;
            this.dataLastRefreshed = dataLastRefreshed;
            this.cacheHit = cacheHit;
        }

        public List<College> getColleges() {
            return colleges;
        }

        public String getSource() {
            return source;
        }

        public String getDataLastRefreshed() {
            return dataLastRefreshed;
        }

        public boolean isCacheHit() {
            return cacheHit;
        }
    }

    private static class FetchResult {
        private final List<College> colleges;
        private final String fetchedAt;
        private final boolean fromCache;

        private FetchResult(List<College> colleges, String fetchedAt, boolean fromCache) {
            this.colleges = colleges;
            this.fetchedAt = fetchedAt;
            this.fromCache = fromCache;
        }
    }

    private static class CacheEntry {
        private final List<College> colleges;
        private final String fetchedAt;
        private final Instant expiresAt;

        private CacheEntry(List<College> colleges, String fetchedAt, Instant expiresAt) {
            this.colleges = colleges;
            this.fetchedAt = fetchedAt;
            this.expiresAt = expiresAt;
        }
    }
}
