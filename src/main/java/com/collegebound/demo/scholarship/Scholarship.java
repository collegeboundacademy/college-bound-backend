package com.collegebound.demo.scholarship;
//scraper that gets any website
import java.util.List;

public class Scholarship {
    private final String name;
    private final String deadline;
    private final String award;
    private final String link;
    private final List<String> questions; 

    public Scholarship(String name, String deadline, String award, String link, List<String> questions) {
        this.name = name;
        this.deadline = deadline;
        this.award = award;
        this.link = link;
        this.questions = questions; 
    }

    public String getName()     { return name; }
    public String getDeadline() { return deadline; }
    public String getAward()    { return award; }
    public String getLink()     { return link; }
    public List<String> getQuestions() { return questions; } 
}