package com.cinedaltons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerDto {
    private String sessionId;
    private int questionIndex;
    private int selectedAnswer;
}