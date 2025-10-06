package org.example.mizzyquiz.quiz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.quiz.dto.*;
import org.example.mizzyquiz.quiz.enitity.Question;
import org.example.mizzyquiz.quiz.enitity.QuestionType;
import org.example.mizzyquiz.quiz.enitity.Quiz;
import org.example.mizzyquiz.quiz.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizScoringService {

    private final QuizRepository quizRepository;

    @Transactional
    public QuizScoreResponse submitQuizAnswers(Long quizId, QuizSubmissionRequest request) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();

        int score = 0;
        int total = 0;

        for (Question question : quiz.getQuestions()) {
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
