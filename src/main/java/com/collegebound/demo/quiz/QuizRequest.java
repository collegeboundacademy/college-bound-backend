package com.collegebound.demo.quiz;

import java.util.List;

public class QuizRequest {

    private String title;
    private String description;
    private List<Question> questions;

    public QuizRequest() {}

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}