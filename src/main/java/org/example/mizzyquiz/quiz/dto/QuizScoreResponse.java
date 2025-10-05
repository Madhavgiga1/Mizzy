package org.example.mizzyquiz.quiz.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizScoreResponse {
    private Integer score;
    private Integer total;
}
