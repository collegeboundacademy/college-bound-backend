package com.collegebound.demo.questboard;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collegebound.demo.user.User;
import com.collegebound.demo.user.UserRepository;

import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/quest-board")
public class QuestBoardController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private QuestBoardProgressRepository progressRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/state")
    public ResponseEntity<?> getState(Authentication auth) {
        User user = resolveUser(auth);

        Optional<QuestBoardProgress> progress = progressRepo.findByUser(user);
        if (progress.isEmpty()) {
            return ResponseEntity.ok(Map.of("notFound", true));
        }

        QuestBoardProgress saved = progress.get();

        Map<String, Object> payload = new LinkedHashMap<>();
        Map<String, Object> player = new LinkedHashMap<>();
        player.put("name", user.getUsername());
        player.put("level", saved.getLevel());
        player.put("xp", saved.getXp());
        player.put("xpNeeded", saved.getXpNeeded());
        player.put("coins", saved.getCoins());
        player.put("streak", saved.getStreak());

        payload.put("player", player);
        payload.put("dailyQuests", parseJsonOrDefault(saved.getDailyQuestsJson(), "[]"));
        payload.put("majorQuests", parseJsonOrDefault(saved.getMajorQuestsJson(), "[]"));
        payload.put("bonusQuest", parseJsonOrDefault(saved.getBonusQuestJson(), "{}"));
        payload.put("badges", parseJsonOrDefault(saved.getBadgesJson(), "[]"));

        return ResponseEntity.ok(payload);
    }

    @PutMapping("/state")
    public ResponseEntity<?> putState(@RequestBody QuestBoardStateRequest body, Authentication auth) {
        if (body.getPlayer() == null
                || body.getDailyQuests() == null
                || body.getMajorQuests() == null
                || body.getBonusQuest() == null
                || body.getBadges() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }

        User user = resolveUser(auth);
        QuestBoardProgress progress = progressRepo.findByUser(user).orElseGet(QuestBoardProgress::new);

        progress.setUser(user);
        progress.setLevel(body.getPlayer().getLevel());
        progress.setXp(body.getPlayer().getXp());
        progress.setXpNeeded(body.getPlayer().getXpNeeded());
        progress.setCoins(body.getPlayer().getCoins());
        progress.setStreak(body.getPlayer().getStreak());
        progress.setDailyQuestsJson(body.getDailyQuests().toString());
        progress.setMajorQuestsJson(body.getMajorQuests().toString());
        progress.setBonusQuestJson(body.getBonusQuest().toString());
        progress.setBadgesJson(body.getBadges().toString());

        if (body.getPlayer().getName() != null && !body.getPlayer().getName().isBlank()) {
            user.setUsername(body.getPlayer().getName().trim());
            userRepo.save(user);
        }

        progressRepo.save(progress);

        return ResponseEntity.ok(Map.of("saved", true));
    }

    private Object parseJsonOrDefault(String rawJson, String fallbackJson) {
        try {
            String source = (rawJson == null || rawJson.isBlank()) ? fallbackJson : rawJson;
            return objectMapper.readTree(source);
        } catch (RuntimeException e) {
            try {
                return objectMapper.readTree(fallbackJson);
            } catch (RuntimeException second) {
                return null;
            }
        }
    }

    private User resolveUser(Authentication auth) {
        String principal = auth.getName();

        Optional<User> byUsername = userRepo.findByUsername(principal);
        if (byUsername.isPresent()) {
            return byUsername.get();
        }

        try {
            return userRepo.findByGithubId(Integer.valueOf(principal))
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } catch (NumberFormatException ex) {
            throw new RuntimeException("User not found");
        }
    }
}
