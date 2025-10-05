package org.example.mizzyquiz.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuizQuestionsResponse {
    private Long quizId;
    private String quizTitle;
    private String description;
    private Integer totalPoints;
    private Integer questionCount;
    private List<UserQuestionDTO> questions;
}
