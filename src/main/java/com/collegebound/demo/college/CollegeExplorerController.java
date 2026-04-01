package com.collegebound.demo.college;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/colleges")
public class CollegeExplorerController {

    @Autowired
    private CollegeScorecardClient scorecardClient;

        private final List<College> fallbackColleges = Arrays.asList(
            new College("usc", "University of Southern California", "CA", "Private", 0.12, 41200, 0.96, 1470, 33,
                21000, 0.92, 65000, "https://www.usc.edu", 9.0, 0.10, 0.05,
                0.48, 0.52, 0.28, 0.06, 0.18, 0.22, 0.08, 0.03),
            new College("ucla", "University of California, Los Angeles", "CA", "Public", 0.09, 17300, 0.95, 1400,
                31, 33000, 0.91, 68000, "https://www.ucla.edu", 15.0, 0.15, 0.04,
                0.45, 0.55, 0.22, 0.04, 0.20, 0.31, 0.09, 0.03),
            new College("ut-austin", "The University of Texas at Austin", "TX", "Public", 0.31, 18200, 0.94,
                1360, 30, 42000, 0.91, 52000, "https://www.utexas.edu", 20.0, 0.20, 0.05,
                0.47, 0.53, 0.34, 0.05, 0.24, 0.22, 0.07, 0.03),
            new College("georgia-tech", "Georgia Institute of Technology", "GA", "Public", 0.18, 17100, 0.97,
                1450, 33, 19000, 0.94, 75000, "https://www.gatech.edu", 19.0, 0.18, 0.03,
                0.61, 0.39, 0.39, 0.07, 0.09, 0.24, 0.06, 0.02),
            new College("penn-state", "Penn State University", "PA", "Public", 0.54, 26500, 0.91, 1260, 28,
                41000, 0.87, 48000, "https://www.psu.edu", 17.0, 0.25, 0.12,
                0.51, 0.49, 0.68, 0.06, 0.09, 0.08, 0.05, 0.04),
            new College("arizona-state", "Arizona State University", "AZ", "Public", 0.89, 14500, 0.87, 1210,
                25, 65000, 0.78, 38000, "https://www.asu.edu", 23.0, 0.35, 0.15,
                0.49, 0.51, 0.43, 0.05, 0.26, 0.08, 0.07, 0.04),
            new College("howard", "Howard University", "DC", "Private", 0.35, 24200, 0.89, 1180, 25,
                11000, 0.85, 35000, "https://www.howard.edu", 14.0, 0.65, 0.08,
                0.29, 0.71, 0.03, 0.76, 0.05, 0.02, 0.03, 0.02),
            new College("spelman", "Spelman College", "GA", "Private", 0.51, 28600, 0.90, 1130, 24,
                2400, 0.88, 42000, "https://www.spelman.edu", 12.0, 0.50, 0.05,
                0.00, 1.00, 0.01, 0.82, 0.03, 0.01, 0.03, 0.02),
            new College("fsu", "Florida State University", "FL", "Public", 0.25, 12200, 0.93, 1320, 29,
                32000, 0.89, 45000, "https://www.fsu.edu", 18.0, 0.22, 0.08,
                0.44, 0.56, 0.56, 0.10, 0.19, 0.03, 0.05, 0.03),
            new College("purdue", "Purdue University", "IN", "Public", 0.53, 12900, 0.92, 1310, 29,
                38000, 0.88, 55000, "https://www.purdue.edu", 16.0, 0.20, 0.05,
                0.57, 0.43, 0.57, 0.03, 0.08, 0.14, 0.05, 0.03),
            new College("northwestern", "Northwestern University", "IL", "Private", 0.07, 30800, 0.98, 1500,
                34, 8600, 0.96, 80000, "https://www.northwestern.edu", 8.0, 0.15, 0.04,
                0.47, 0.53, 0.34, 0.06, 0.16, 0.18, 0.07, 0.03),
            new College("duke", "Duke University", "NC", "Private", 0.06, 26800, 0.98, 1510, 34, 6900,
                0.96, 82000, "https://www.duke.edu", 7.0, 0.12, 0.03,
                0.48, 0.52, 0.43, 0.09, 0.11, 0.13, 0.06, 0.03));

    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "maxNetPrice", required = false) Integer maxNetPrice,
            @RequestParam(name = "limit", defaultValue = "20") int limit) {
        int boundedLimit = Math.max(1, Math.min(limit, 100));

        Optional<CollegeScorecardClient.SearchResult> liveSearch = scorecardClient.search(q, state, boundedLimit);

        List<College> sourceColleges = liveSearch.map(CollegeScorecardClient.SearchResult::getColleges)
                .orElse(fallbackColleges);

        String query = normalize(q);
        String stateFilter = normalize(state);
        String typeFilter = normalize(type);

        List<College> filtered = sourceColleges.stream()
                .filter(c -> query.isBlank() || containsIgnoreCase(c.getName(), query))
                .filter(c -> stateFilter.isBlank() || c.getState().equalsIgnoreCase(stateFilter))
                .filter(c -> typeFilter.isBlank() || c.getType().equalsIgnoreCase(typeFilter))
                .filter(c -> maxNetPrice == null || c.getAverageNetPrice() <= maxNetPrice)
                .limit(boundedLimit)
                .toList();

        if (liveSearch.isPresent()) {
            CollegeScorecardClient.SearchResult live = liveSearch.get();
            return Map.of(
                    "count", filtered.size(),
                    "results", filtered,
                    "source", live.getSource(),
                    "dataLastRefreshed", live.getDataLastRefreshed(),
                    "cache", Map.of("hit", live.isCacheHit()));
        }

        return Map.of(
                "count", filtered.size(),
                "results", filtered,
                "source", "Fallback sample dataset (set COLLEGE_SCORECARD_API_KEY for live data)",
                "dataLastRefreshed", "Local sample data");
    }

    @GetMapping("/{id}")
    public Map<String, Object> getCollege(@PathVariable("id") String id) {
        Optional<College> college = findCollegeByIdWithConfiguredSource(id);

        if (college.isEmpty()) {
            return Map.of("error", "College not found");
        }

        return Map.of("college", college.get());
    }

    @GetMapping("/compare")
    public Map<String, Object> compare(
            @RequestParam(name = "ids") String ids,
            @RequestParam(name = "gpa", required = false) Double gpa,
            @RequestParam(name = "sat", required = false) Integer sat,
            @RequestParam(name = "act", required = false) Integer act,
            @RequestParam(name = "budget", required = false) Integer budget) {
        List<String> requestedIds = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .limit(4)
                .toList();

        List<Map<String, Object>> payload = new ArrayList<>();

        for (String collegeId : requestedIds) {
            findCollegeByIdWithConfiguredSource(collegeId)
                    .ifPresent(college -> payload.add(buildCompareItem(college, gpa, sat, act, budget)));
        }

        return Map.of("count", payload.size(), "results", payload);
    }

    private Map<String, Object> buildCompareItem(College college, Double gpa, Integer sat, Integer act, Integer budget) {
        Map<String, String> fit = new LinkedHashMap<>();

        fit.put("academic", classifyAcademicFit(college, gpa, sat, act));
        fit.put("financial", classifyFinancialFit(college, budget));
        fit.put("outcomes", classifyOutcomeFit(college));

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("college", college);
        item.put("fit", fit);
        return item;
    }

    private String classifyAcademicFit(College college, Double gpa, Integer sat, Integer act) {
        boolean hasSat = sat != null;
        boolean hasAct = act != null;

        if (!hasSat && !hasAct) {
            return "Unknown";
        }

        int score = 0;
        int checks = 0;

        if (hasSat && college.getSatMidpoint() != null) {
            score += sat >= college.getSatMidpoint() ? 1 : 0;
            checks++;
        }

        if (hasAct && college.getActMidpoint() != null) {
            score += act >= college.getActMidpoint() ? 1 : 0;
            checks++;
        }

        if (checks == 0) {
            return "Unknown";
        }

        double ratio = checks == 0 ? 0 : (double) score / checks;

        if (ratio >= 0.8) {
            return "Strong";
        }

        if (ratio >= 0.5) {
            return "Target";
        }

        return "Reach";
    }

    private String classifyFinancialFit(College college, Integer budget) {
        if (budget == null || college.getAverageNetPrice() == null) {
            return "Unknown";
        }

        if (college.getAverageNetPrice() <= budget) {
            return "Within Budget";
        }

        if (college.getAverageNetPrice() <= budget + 5000) {
            return "Stretch";
        }

        return "Over Budget";
    }

    private String classifyOutcomeFit(College college) {
        if (college.getRetentionRate() == null) {
            return "Unknown";
        }

        if (college.getRetentionRate() >= 0.95) {
            return "Excellent";
        }

        if (college.getRetentionRate() >= 0.90) {
            return "Strong";
        }

        return "Developing";
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }

        private Optional<College> findCollegeByIdWithConfiguredSource(String collegeId) {
        return scorecardClient.findById(collegeId)
                .or(() -> fallbackColleges.stream()
                        .filter(c -> c.getId().equalsIgnoreCase(collegeId))
                        .findFirst());
    }
}
