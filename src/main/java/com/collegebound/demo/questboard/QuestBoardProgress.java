package com.collegebound.demo.questboard;

import java.util.Date;

import com.collegebound.demo.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;

@Entity
public class QuestBoardProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private int level;
    private int xp;
    private int xpNeeded;
    private int coins;
    private int streak;

    @Column(columnDefinition = "TEXT")
    private String dailyQuestsJson;

    @Column(columnDefinition = "TEXT")
    private String majorQuestsJson;

    @Column(columnDefinition = "TEXT")
    private String bonusQuestJson;

    @Column(columnDefinition = "TEXT")
    private String badgesJson;

    private Date updatedAt;

    public QuestBoardProgress() {
        this.updatedAt = new Date();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getXpNeeded() {
        return xpNeeded;
    }

    public void setXpNeeded(int xpNeeded) {
        this.xpNeeded = xpNeeded;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public String getDailyQuestsJson() {
        return dailyQuestsJson;
    }

    public void setDailyQuestsJson(String dailyQuestsJson) {
        this.dailyQuestsJson = dailyQuestsJson;
    }

    public String getMajorQuestsJson() {
        return majorQuestsJson;
    }

    public void setMajorQuestsJson(String majorQuestsJson) {
        this.majorQuestsJson = majorQuestsJson;
    }

    public String getBonusQuestJson() {
        return bonusQuestJson;
    }

    public void setBonusQuestJson(String bonusQuestJson) {
        this.bonusQuestJson = bonusQuestJson;
    }

    public String getBadgesJson() {
        return badgesJson;
    }

    public void setBadgesJson(String badgesJson) {
        this.badgesJson = badgesJson;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
