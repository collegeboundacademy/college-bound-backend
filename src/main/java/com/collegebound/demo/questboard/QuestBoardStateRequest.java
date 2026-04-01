package com.collegebound.demo.questboard;

import tools.jackson.databind.JsonNode;

public class QuestBoardStateRequest {
    private PlayerState player;
    private JsonNode dailyQuests;
    private JsonNode majorQuests;
    private JsonNode bonusQuest;
    private JsonNode badges;

    public PlayerState getPlayer() {
        return player;
    }

    public void setPlayer(PlayerState player) {
        this.player = player;
    }

    public JsonNode getDailyQuests() {
        return dailyQuests;
    }

    public void setDailyQuests(JsonNode dailyQuests) {
        this.dailyQuests = dailyQuests;
    }

    public JsonNode getMajorQuests() {
        return majorQuests;
    }

    public void setMajorQuests(JsonNode majorQuests) {
        this.majorQuests = majorQuests;
    }

    public JsonNode getBonusQuest() {
        return bonusQuest;
    }

    public void setBonusQuest(JsonNode bonusQuest) {
        this.bonusQuest = bonusQuest;
    }

    public JsonNode getBadges() {
        return badges;
    }

    public void setBadges(JsonNode badges) {
        this.badges = badges;
    }

    public static class PlayerState {
        private String name;
        private int level;
        private int xp;
        private int xpNeeded;
        private int coins;
        private int streak;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
    }
}
