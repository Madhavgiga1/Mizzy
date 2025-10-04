package org.example.mizzyquiz.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDto {
    private UUID attemptId;
    private UUID questionId;
    private boolean saved;
    private String message;
}
