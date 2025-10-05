package org.example.mizzyquiz.quiz.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {
    private UUID questionId;
    private UUID selectedOptionId;
}
