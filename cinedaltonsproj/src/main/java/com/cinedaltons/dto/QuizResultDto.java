package com.cinedaltons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDto {
    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private String message;
    private Long timeTakenSeconds;
}