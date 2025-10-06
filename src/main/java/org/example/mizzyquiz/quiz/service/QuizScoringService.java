package org.example.mizzyquiz.quiz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.attempt.repository.QuizAttemptRepository;
import org.example.mizzyquiz.auth.entity.User;
import org.example.mizzyquiz.auth.repository.UserRepository;
import org.example.mizzyquiz.quiz.dto.*;
import org.example.mizzyquiz.quiz.enitity.*;
import org.example.mizzyquiz.quiz.repository.QuizRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizScoringService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    @Transactional
    public QuizScoreResponse submitQuizAnswers(Long quizId, QuizSubmissionRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        User currentUser = getCurrentUser();

        int score = 0;
        int total = 0;

        // Calculate score
        for (Question question : quiz.getQuestions()) {
            if (question.getType() == QuestionType.TEXT) {
                continue;
            }

            total += question.getPoints();

            // Find user's answer for this question
            AnswerRequest userAnswer = request.getAnswers().stream()
                    .filter(a -> a.getQuestionId().equals(question.getId()))
                    .findFirst()
                    .orElse(null);

            if (userAnswer != null) {
                Option selectedOption = question.getOptions().stream()
                        .filter(opt -> opt.getId().equals(userAnswer.getSelectedOptionId()))
                        .findFirst()
                        .orElse(null);

                if (selectedOption != null && selectedOption.isCorrect()) {
                    score += question.getPoints();
                }
            }
        }


        double percentage = total > 0 ? (score * 100.0) / total : 0.0;
        boolean passed = quiz.getPassingScore() != null && percentage >= quiz.getPassingScore();


        QuizAttempt attempt = QuizAttempt.builder()
                .user(currentUser)
                .quiz(quiz)
                .startedAt(LocalDateTime.now())
                .score(score)
                .totalScore(total)
                .percentage(percentage)
                .passed(passed)
                .build();

        attempt.complete();
        attemptRepository.save(attempt);

        log.info("Quiz submitted - User: {}, Quiz: {}, Score: {}/{}",
                currentUser.getUsername(), quiz.getTitle(), score, total);

        return QuizScoreResponse.builder()
                .score(score)
                .total(total)
                .build();
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
