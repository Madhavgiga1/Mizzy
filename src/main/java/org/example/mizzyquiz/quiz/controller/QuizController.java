package org.example.mizzyquiz.quiz.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.attempt.dto.QuizAttemptDto;
import org.example.mizzyquiz.auth.entity.User;
import org.example.mizzyquiz.auth.repository.UserRepository;
import org.example.mizzyquiz.quiz.dto.QuizQuestionsResponse;
import org.example.mizzyquiz.quiz.dto.QuizScoreResponse;
import org.example.mizzyquiz.quiz.dto.QuizSubmissionRequest;
import org.example.mizzyquiz.quiz.dto.QuizSummaryDTO;
import org.example.mizzyquiz.quiz.enitity.Quiz;
import org.example.mizzyquiz.quiz.enitity.QuizAttempt;
import org.example.mizzyquiz.quiz.repository.QuizAttemptRepository;
import org.example.mizzyquiz.quiz.repository.QuizRepository;
import org.example.mizzyquiz.quiz.service.QuizScoringService;
import org.example.mizzyquiz.quiz.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final QuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    /*@GetMapping("/my-attempts")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<QuizAttemptDto>> getMyAttempts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<QuizAttempt> attempts = attemptRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        List<QuizAttemptDto> history = attempts.stream()
                .map(a -> QuizAttemptDto.builder()
                        .attemptId(a.getId())
                        .quizTitle(a.getQuiz().getTitle())
                        .score(a.getScore())
                        .total(a.getTotalScore())
                        .percentage(a.getPercentage())
                        .passed(a.getPassed())
                        .completedAt(a.getCompletedAt())
                        .build())
                .toList();

        return ResponseEntity.ok(history);
    }*/


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
