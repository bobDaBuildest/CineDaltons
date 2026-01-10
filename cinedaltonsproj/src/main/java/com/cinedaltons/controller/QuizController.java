package com.cinedaltons.controller;

import com.cinedaltons.dto.QuizAnswerDto;
import com.cinedaltons.dto.QuizResultDto;
import com.cinedaltons.dto.QuizSessionDto;
import com.cinedaltons.model.QuizQuestion;
import com.cinedaltons.model.QuizScore;
import com.cinedaltons.model.User;
import com.cinedaltons.repository.UserRepository;
import com.cinedaltons.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;
    private final UserRepository userRepository;

    @Autowired
    public QuizController(QuizService quizService, UserRepository userRepository) {
        this.quizService = quizService;
        this.userRepository = userRepository;
    }

    /**
     * Δημιουργεί νέα quiz session
     * Accessible μόνο από authenticated users
     */
    @PostMapping("/start")
    public ResponseEntity<QuizSessionDto> startQuiz(
            @RequestParam(defaultValue = "10") int numberOfQuestions,
            @AuthenticationPrincipal UserDetails userDetails) {

        String sessionId = quizService.createQuizSession(numberOfQuestions);
        List<QuizQuestion> questions = quizService.getQuizQuestions(sessionId);

        QuizSessionDto response = new QuizSessionDto(sessionId, questions, questions.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Υποβάλλει μια απάντηση
     */
    @PostMapping("/answer")
    public ResponseEntity<Boolean> submitAnswer(
            @RequestBody QuizAnswerDto answer,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean isCorrect = quizService.submitAnswer(
                answer.getSessionId(),
                answer.getQuestionIndex(),
                answer.getSelectedAnswer()
        );

        return ResponseEntity.ok(isCorrect);
    }

    /**
     * Ολοκληρώνει το quiz και αποθηκεύει το σκορ
     */
    @PostMapping("/complete/{sessionId}")
    public ResponseEntity<QuizResultDto> completeQuiz(
            @PathVariable String sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Βρίσκουμε τον χρήστη
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        QuizScore score = quizService.completeQuiz(sessionId, user);

        QuizResultDto result = new QuizResultDto();
        result.setScore(score.getScoreAchieved());
        result.setTimeTakenSeconds(score.getTimeTakenSeconds());
        result.setMessage("Συγχαρητήρια! Το σκορ σου: " + score.getScoreAchieved() + "/100");

        return ResponseEntity.ok(result);
    }

    /**
     * Leaderboard - Top 10
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<List<QuizScore>> getLeaderboard() {
        return ResponseEntity.ok(quizService.getLeaderboard());
    }

    /**
     * Ιστορικό του authenticated χρήστη
     */
    @GetMapping("/history")
    public ResponseEntity<List<QuizScore>> getMyHistory(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ResponseEntity.ok(quizService.getUserHistory(user));
    }
}