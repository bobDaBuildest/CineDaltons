package com.cinedaltons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {
    private String question;
    private List<String> options;
    private int correctAnswerIndex;
    private String category; // e.g., "actors", "plot", "year", "trivia"
    private String movieTitle;
}