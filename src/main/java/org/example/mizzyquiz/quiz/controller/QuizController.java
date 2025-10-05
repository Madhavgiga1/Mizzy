package org.example.mizzyquiz.quiz.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.quiz.dto.QuizQuestionsResponse;
import org.example.mizzyquiz.quiz.dto.QuizScoreResponse;
import org.example.mizzyquiz.quiz.dto.QuizSubmissionRequest;
import org.example.mizzyquiz.quiz.dto.QuizSummaryDTO;
import org.example.mizzyquiz.quiz.enitity.Quiz;
import org.example.mizzyquiz.quiz.repository.QuizRepository;
import org.example.mizzyquiz.quiz.service.QuizScoringService;
import org.example.mizzyquiz.quiz.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;
    private final QuizScoringService scoringService;
    private final QuizRepository quizRepository;



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

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<QuizScoreResponse> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizSubmissionRequest request) {

        QuizScoreResponse response = scoringService.submitQuizAnswers(quizId, request);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<QuizSummaryDTO>> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();

        List<QuizSummaryDTO> summaries = quizzes.stream()
                .map(quiz -> QuizSummaryDTO.builder()
                        .id(quiz.getId())
                        .title(quiz.getTitle())
                        .description(quiz.getDescription())
                        .questionCount(quiz.getQuestionCount())
                        .totalPoints(quiz.getTotalPoints())
                        .build())
                .toList();

        return ResponseEntity.ok(summaries);
    }
}
