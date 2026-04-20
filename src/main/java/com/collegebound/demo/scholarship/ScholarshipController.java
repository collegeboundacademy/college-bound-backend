package com.collegebound.demo.scholarship;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController = @Controller + @ResponseBody
// Means: "this class handles HTTP requests AND automatically
// converts return values to JSON" — no extra work needed
@RestController

// All methods in this class live under /api/scholarships
@RequestMapping("/api/scholarships")
public class ScholarshipController {

    // We don't use "new ScraperService()" here
    // Spring sees the constructor parameter and says "I have one of those, let me inject it"
    // This is called Dependency Injection — tourist version: Spring does the wiring for you
    private final ScraperService scraperService;

    public ScholarshipController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    // Handles: GET /api/scholarships
    // Spring takes the List<Scholarship> we return and converts it to a JSON array automatically
    @GetMapping
    public List<Scholarship> getScholarships() {
        return scraperService.scrape();
    }
}