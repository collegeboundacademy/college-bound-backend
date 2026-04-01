package com.collegebound.demo.questboard;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.collegebound.demo.user.User;

public interface QuestBoardProgressRepository extends JpaRepository<QuestBoardProgress, Long> {
    Optional<QuestBoardProgress> findByUser(User user);
}
