package com.collegebound.demo.college;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class CollegeCsvRepository {

    private final ResourceLoader resourceLoader;

    @Value("${college.csv.path:classpath:data/colleges.csv}")
    private String csvPath;

    public CollegeCsvRepository(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Optional<CsvData> load() {
        try (Reader reader = openReader()) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build()
                    .parse(reader);

            List<College> colleges = new ArrayList<>();
            String lastRefreshed = "CSV dataset";

            for (CSVRecord record : records) {
                College college = parseCollege(record);
                if (college == null) {
                    continue;
                }

                colleges.add(college);
                String rowRefreshed = get(record, "dataLastRefreshed");
                if (rowRefreshed != null && !rowRefreshed.isBlank()) {
                    lastRefreshed = rowRefreshed;
                }
            }

            if (colleges.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(new CsvData(colleges, lastRefreshed));
        } catch (IOException | RuntimeException e) {
            return Optional.empty();
        }
    }

    private Reader openReader() throws IOException {
        if (csvPath.startsWith("classpath:")) {
            Resource resource = resourceLoader.getResource(csvPath);
            InputStream stream = resource.getInputStream();
            return new InputStreamReader(stream, StandardCharsets.UTF_8);
        }

        Path path = Path.of(csvPath);
        return Files.newBufferedReader(path, StandardCharsets.UTF_8);
    }

    private College parseCollege(CSVRecord record) {
        String id = get(record, "id");
        String name = get(record, "name");

        if (id == null || id.isBlank() || name == null || name.isBlank()) {
            return null;
        }

        return new College(
            id,
            name,
            get(record, "state"),
            get(record, "type"),
            parseDouble(get(record, "acceptanceRate")),
            parseInteger(get(record, "averageNetPrice")),
            parseDouble(get(record, "retentionRate")),
            parseInteger(get(record, "satMidpoint")),
            parseInteger(get(record, "actMidpoint")),
            parseInteger(get(record, "undergradEnrollment")),
            parseDouble(get(record, "graduationRate4Year")),
            parseInteger(get(record, "earningsMedian6Yrs")),
            get(record, "website"),
            parseDouble(get(record, "studentFacultyRatio")),
            parseDouble(get(record, "firstGenerationShare")),
                parseDouble(get(record, "partTimeShare")),
                parseDouble(get(record, "menShare")),
                parseDouble(get(record, "womenShare")),
                parseDouble(get(record, "raceWhiteShare")),
                parseDouble(get(record, "raceBlackShare")),
                parseDouble(get(record, "raceHispanicShare")),
                parseDouble(get(record, "raceAsianShare")),
                parseDouble(get(record, "raceTwoOrMoreShare")),
                parseDouble(get(record, "raceUnknownShare")));
    }

    private String get(CSVRecord record, String key) {
        try {
            return record.get(key);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Double.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static class CsvData {
        private final List<College> colleges;
        private final String dataLastRefreshed;

        public CsvData(List<College> colleges, String dataLastRefreshed) {
            this.colleges = colleges;
            this.dataLastRefreshed = dataLastRefreshed;
        }

        public List<College> getColleges() {
            return colleges;
        }

        public String getDataLastRefreshed() {
            return dataLastRefreshed;
        }
    }
}
