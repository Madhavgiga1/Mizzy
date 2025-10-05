package org.example.mizzyquiz.quiz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.quiz.dto.*;
import org.example.mizzyquiz.quiz.enitity.Question;
import org.example.mizzyquiz.quiz.enitity.Quiz;
import org.example.mizzyquiz.quiz.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizScoringService {

    private final QuizRepository quizRepository;

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
