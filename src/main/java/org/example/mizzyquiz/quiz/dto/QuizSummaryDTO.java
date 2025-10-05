package org.example.mizzyquiz.quiz.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private Integer questionCount;
    private Integer totalPoints;
}
