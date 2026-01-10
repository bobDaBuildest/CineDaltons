package com.cinedaltons.repository;

import com.cinedaltons.model.QuizScore;
import com.cinedaltons.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizScoreRepository extends JpaRepository<QuizScore, Long> {

    // Βρες όλα τα scores ενός χρήστη (ταξινομημένα από το νεότερο)
    List<QuizScore> findByUserOrderBySessionDateDesc(User user);

    // Top 10 leaderboard
    @Query("SELECT q FROM QuizScore q ORDER BY q.scoreAchieved DESC, q.timeTakenSeconds ASC")
    List<QuizScore> findTop10ByOrderByScoreAchievedDescTimeTakenSecondsAsc();

    // Καλύτερο σκορ χρήστη
    @Query("SELECT MAX(q.scoreAchieved) FROM QuizScore q WHERE q.user = :user")
    Integer findBestScoreByUser(User user);
}