package org.example.mizzyquiz.quiz;

import org.example.mizzyquiz.quiz.dto.*;
import org.example.mizzyquiz.quiz.enitity.*;
import org.example.mizzyquiz.quiz.repository.QuizRepository;
import org.example.mizzyquiz.quiz.service.QuizScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizScoringServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizScoringService scoringService;

    private Quiz quiz;
    private Question question;
    private UUID questionId;
    private UUID correctOptionId;
    private UUID wrongOptionId;

    @BeforeEach
    void setUp() {
        questionId = UUID.randomUUID();
        correctOptionId = UUID.randomUUID();
        wrongOptionId = UUID.randomUUID();

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");

        question = Question.builder()
                .id(questionId)
                .text("What is 2+2?")
                .points(5)
                .type(QuestionType.SINGLE_CHOICE)
                .options(new ArrayList<>())
                .build();

        Option correct = Option.builder()
                .id(correctOptionId)
                .text("4")
                .correct(true)
                .build();

        Option wrong = Option.builder()
                .id(wrongOptionId)
                .text("5")
                .correct(false)
                .build();

        question.getOptions().add(correct);
        question.getOptions().add(wrong);
        quiz.addQuestion(question);
    }

    @Test
    void shouldReturnPerfectScore() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        QuizSubmissionRequest request = QuizSubmissionRequest.builder()
                .answers(List.of(
                        AnswerRequest.builder()
                                .questionId(questionId)
                                .selectedOptionId(correctOptionId)
                                .build()
                ))
                .build();

        QuizScoreResponse response = scoringService.submitQuizAnswers(1L, request);

        assertThat(response.getScore()).isEqualTo(5);
        assertThat(response.getTotal()).isEqualTo(5);
    }

    @Test
    void shouldReturnZeroScore() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        QuizSubmissionRequest request = QuizSubmissionRequest.builder()
                .answers(List.of(
                        AnswerRequest.builder()
                                .questionId(questionId)
                                .selectedOptionId(wrongOptionId)
                                .build()
                ))
                .build();

        QuizScoreResponse response = scoringService.submitQuizAnswers(1L, request);

        assertThat(response.getScore()).isEqualTo(0);
        assertThat(response.getTotal()).isEqualTo(5);
    }
}
