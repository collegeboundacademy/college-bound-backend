package com.collegebound.demo.college;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/colleges/persistence")
public class CollegeExplorerPersistenceController {

    private final ConcurrentMap<String, PersistenceRecord> store = new ConcurrentHashMap<>();

    @GetMapping
    public Map<String, Object> read(@RequestParam(name = "studentKey") String studentKey) {
        String key = normalize(studentKey);
        if (key.isBlank()) {
            return Map.of("error", "studentKey is required");
        }

        PersistenceRecord record = store.get(key);
        if (record == null) {
            return Map.of(
                "studentKey", key,
                "profile", Map.of(),
                "savedColleges", List.of(),
                "updatedAt", "");
        }

        return Map.of(
            "studentKey", key,
            "profile", record.profile(),
            "savedColleges", record.savedColleges(),
            "updatedAt", record.updatedAt());
    }

    @PutMapping
    public Map<String, Object> write(@RequestBody PersistenceRequest request) {
        String key = normalize(request.studentKey());
        if (key.isBlank()) {
            return Map.of("error", "studentKey is required");
        }

        Map<String, Object> profile = request.profile() == null ? Map.of() : request.profile();
        List<Map<String, Object>> savedColleges = request.savedColleges() == null ? List.of() : request.savedColleges();
        String updatedAt = Instant.now().toString();

        PersistenceRecord record = new PersistenceRecord(profile, savedColleges, updatedAt);
        store.put(key, record);

        return Map.of(
            "ok", true,
            "studentKey", key,
            "updatedAt", updatedAt,
            "savedCount", savedColleges.size());
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    public record PersistenceRequest(
        String studentKey,
        Map<String, Object> profile,
        List<Map<String, Object>> savedColleges) {
    }

    private record PersistenceRecord(
        Map<String, Object> profile,
        List<Map<String, Object>> savedColleges,
        String updatedAt) {
    }
}