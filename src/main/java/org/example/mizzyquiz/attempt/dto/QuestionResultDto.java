package org.example.mizzyquiz.attempt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultDto {
    private UUID questionId;
    private String questionText;
    private boolean correct;
    private int pointsEarned;
    private int maxPoints;
    private String explanation;
}
