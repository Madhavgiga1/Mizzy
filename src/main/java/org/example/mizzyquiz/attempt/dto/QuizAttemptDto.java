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
    private UUID id;
    private Long quizId;
    private String quizTitle;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private AttemptStatus status;
    private Integer score;
    private Integer totalScore;
    private Double percentage;
    private Boolean passed;
}
