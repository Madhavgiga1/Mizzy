package org.example.mizzyquiz.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizPlayDto {
    private UUID attemptId;
    private String quizTitle;
    private String description;
    private List<QuestionDto> questions;
    private Integer timeLimitMinutes;
    private LocalDateTime startedAt;
    private int totalQuestions;
}
