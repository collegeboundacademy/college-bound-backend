package com.collegebound.demo.user;

import java.util.Date;
import java.util.List;

import com.collegebound.demo.quiz.Quiz;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer githubId;
    private String username;
    private String provider;
    private Date createdAt;
    
    @OneToMany(mappedBy = "creator")
    private List<Quiz> quizzesCreated;

    public User() {
        this.createdAt = new Date();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getGithubId() {
        return githubId;
    }
    public void setGithubId(Integer githubId) {
        this.githubId = githubId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public List<Quiz> getQuizzesCreated() {
        return quizzesCreated;
    }
    public void setQuizzesCreated(List<Quiz> quizzesCreated) {
        this.quizzesCreated = quizzesCreated;
    }
}