package org.example.mizzyquiz.quiz;

import org.example.mizzyquiz.auth.entity.User;
import org.example.mizzyquiz.auth.repository.UserRepository;
import org.example.mizzyquiz.quiz.dto.*;
import org.example.mizzyquiz.quiz.enitity.*;
import org.example.mizzyquiz.quiz.repository.*;
import org.example.mizzyquiz.quiz.service.QuizScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizScoringServiceTest {

    @Mock
    private QuizRepository quizRepository;



    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private QuizScoringService scoringService;

    private Quiz quiz;
    private User user;
    private Question question1;
    private Question question2;
    private UUID q1Id;
    private UUID q2Id;
    private UUID q1CorrectId;
    private UUID q1WrongId;
    private UUID q2CorrectId;
    private UUID q2WrongId;

    @BeforeEach
    void setUp() {
        q1Id = UUID.randomUUID();
        q2Id = UUID.randomUUID();
        q1CorrectId = UUID.randomUUID();
        q1WrongId = UUID.randomUUID();
        q2CorrectId = UUID.randomUUID();
        q2WrongId = UUID.randomUUID();

        // Mock user
        user = new User();

        user.setUsername("testuser");

        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Create quiz
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setPassingScore(60);

        // Question 1 (2 points)
        question1 = Question.builder()
                .id(q1Id)
                .text("What is 2+2?")
                .points(2)
                .type(QuestionType.SINGLE_CHOICE)
                .options(new ArrayList<>())
                .build();

        Option q1Opt1 = Option.builder()
                .id(q1CorrectId)
                .text("4")
                .correct(true)
                .question(question1)
                .build();

        Option q1Opt2 = Option.builder()
                .id(q1WrongId)
                .text("5")
                .correct(false)
                .question(question1)
                .build();

        question1.getOptions().add(q1Opt1);
        question1.getOptions().add(q1Opt2);

        // Question 2 (3 points)
        question2 = Question.builder()
                .id(q2Id)
                .text("Capital of France?")
                .points(3)
                .type(QuestionType.SINGLE_CHOICE)
                .options(new ArrayList<>())
                .build();

        Option q2Opt1 = Option.builder()
                .id(q2CorrectId)
                .text("Paris")
                .correct(true)
                .question(question2)
                .build();

        Option q2Opt2 = Option.builder()
                .id(q2WrongId)
                .text("London")
                .correct(false)
                .question(question2)
                .build();

        question2.getOptions().add(q2Opt1);
        question2.getOptions().add(q2Opt2);

        quiz.addQuestion(question1);
        quiz.addQuestion(question2);
    }

    @Test
    void shouldReturnPerfectScore() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));


        QuizSubmissionRequest request = QuizSubmissionRequest.builder()
                .answers(Arrays.asList(
                        AnswerRequest.builder()
                                .questionId(q1Id)
                                .selectedOptionId(q1CorrectId)
                                .build(),
                        AnswerRequest.builder()
                                .questionId(q2Id)
                                .selectedOptionId(q2CorrectId)
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
                .answers(Arrays.asList(
                        AnswerRequest.builder()
                                .questionId(q1Id)
                                .selectedOptionId(q1WrongId)
                                .build(),
                        AnswerRequest.builder()
                                .questionId(q2Id)
                                .selectedOptionId(q2WrongId)
                                .build()
                ))
                .build();

        QuizScoreResponse response = scoringService.submitQuizAnswers(1L, request);

        assertThat(response.getScore()).isEqualTo(0);
        assertThat(response.getTotal()).isEqualTo(5);
    }

    @Test
    void shouldReturnPartialScore() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));


        QuizSubmissionRequest request = QuizSubmissionRequest.builder()
                .answers(Arrays.asList(
                        AnswerRequest.builder()
                                .questionId(q1Id)
                                .selectedOptionId(q1CorrectId)
                                .build(),
                        AnswerRequest.builder()
                                .questionId(q2Id)
                                .selectedOptionId(q2WrongId)
                                .build()
                ))
                .build();

        QuizScoreResponse response = scoringService.submitQuizAnswers(1L, request);

        assertThat(response.getScore()).isEqualTo(2);
        assertThat(response.getTotal()).isEqualTo(5);
    }
}
