package org.example.mizzyquiz.quiz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerDto {
    @NotNull(message = "Question ID is required")
    private UUID questionId;

    // For single choice questions
    private UUID selectedOptionId;

    // For multiple choice questions
    private Set<UUID> selectedOptionIds;
}
