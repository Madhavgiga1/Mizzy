package org.example.mizzyquiz.quiz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.auth.entity.User;
import org.example.mizzyquiz.auth.repository.UserRepository;
import org.example.mizzyquiz.quiz.dto.*;
import org.example.mizzyquiz.quiz.enitity.*;
import org.example.mizzyquiz.quiz.repository.QuizAttemptRepository;
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

    /*@Transactional
    public QuizScoreResponse submitQuizAnswers(Long quizId, QuizSubmissionRequest request) {

        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        User currentUser = getCurrentUser();

        // Create attempt record
        QuizAttempt attempt = QuizAttempt.builder()
                .user(currentUser)
                .quiz(quiz)
                .startedAt(LocalDateTime.now())
                .status(AttemptStatus.IN_PROGRESS)
                .build();

        int score = 0;
        int total = 0;

        for (Question question : quiz.getQuestions()) {
            total += question.getPoints();
            // Skip text questions
            if (question.getType() == QuestionType.TEXT) {
                continue;
            }

            total += question.getPoints();

            AnswerRequest userAnswer = request.getAnswers().stream()
                    .filter(a -> a.getQuestionId().equals(question.getId()))
                    .findFirst()
                    .orElse(null);

            if (userAnswer != null) {
                Option selectedOption = question.getOptions().stream()
                        .filter(opt -> opt.getId().equals(userAnswer.getSelectedOptionId()))
                        .findFirst()
                        .orElse(null);

                if (selectedOption != null) {
                    boolean isCorrect = selectedOption.isCorrect();
                    int pointsEarned = isCorrect ? question.getPoints() : 0;

                    if (isCorrect) {
                        score += pointsEarned;
                    }

                    // Save individual answer
                    Answer answer = Answer.builder()
                            .attempt(attempt)
                            .question(question)
                            .selectedOption(selectedOption)
                            .correct(isCorrect)
                            .pointsEarned(pointsEarned)
                            .build();

                    attempt.addAnswer(answer);
                }
            }
        }

        // Calculate results
        double percentage = total > 0 ? (score * 100.0) / total : 0.0;
        boolean passed = quiz.getPassingScore() != null && percentage >= quiz.getPassingScore();

        // Complete the attempt
        attempt.setScore(score);
        attempt.setTotalScore(total);
        attempt.setPercentage(percentage);
        attempt.setPassed(passed);
        attempt.setStatus(AttemptStatus.COMPLETED);
        attempt.complete();


        attemptRepository.save(attempt);

        log.info("Quiz attempt saved - User: {}, Quiz: {}, Score: {}/{}",
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
    }*/

    @Transactional(readOnly = true)
    public QuizScoreResponse submitQuizAnswers(Long quizId, QuizSubmissionRequest request) {

        // Get quiz
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();

        int score = 0;
        int total = 0;

        // Check each answer
        for (Question question : quiz.getQuestions()) {
            total += question.getPoints();

            // Find user's answer for this question
            AnswerRequest userAnswer = request.getAnswers().stream()
                    .filter(a -> a.getQuestionId().equals(question.getId()))
                    .findFirst()
                    .orElse(null);

            if (userAnswer != null) {
                // Check if correct
                boolean isCorrect = question.getOptions().stream()
                        .anyMatch(opt -> opt.getId().equals(userAnswer.getSelectedOptionId()) && opt.isCorrect());

                if (isCorrect) {
                    score += question.getPoints();
                }
            }
        }

        return QuizScoreResponse.builder()
                .score(score)
                .total(total)
                .build();
    }
}
