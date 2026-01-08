package com.cinedaltons.dto;

import com.cinedaltons.model.QuizQuestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSessionDto {
    private String sessionId;
    private List<QuizQuestion> questions;
    private int totalQuestions;
}