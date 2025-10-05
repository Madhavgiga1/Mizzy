package org.example.mizzyquiz.quiz.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionRequest {
    private List<AnswerRequest> answers;
}
