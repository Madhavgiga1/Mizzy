package org.example.mizzyquiz.quiz.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.quiz.dto.QuizQuestionsResponse;
import org.example.mizzyquiz.quiz.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;

    /**
     * Get all questions for a specific quiz
     * Regular users will NOT see correct answers
     * Admins can request to see correct answers by setting includeCorrectAnswers=true
     *
     * Example URLs:
     * - User: GET /api/v1/quizzes/1/questions
     * - Admin: GET /api/v1/quizzes/1/questions?includeCorrectAnswers=true
     */
    @GetMapping("/{quizId}/questions")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<QuizQuestionsResponse> getQuizQuestions(
            @PathVariable Long quizId,
            @RequestParam(required = false, defaultValue = "false") boolean includeCorrectAnswers
    ) {
        log.info("Request to get questions for quiz: {}, includeCorrectAnswers: {}", quizId, includeCorrectAnswers);

        QuizQuestionsResponse response = quizService.getQuizQuestions(quizId, includeCorrectAnswers);

        return ResponseEntity.ok(response);
    }
}
