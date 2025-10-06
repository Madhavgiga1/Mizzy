package org.example.mizzyquiz.attempt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mizzyquiz.quiz.enitity.AttemptStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDto {
    private UUID attemptId;
    private String quizTitle;
    private Integer score;
    private Integer total;
    private Double percentage;
    private Boolean passed;
    private LocalDateTime completedAt;
}
