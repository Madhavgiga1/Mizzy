package org.example.mizzyquiz.attempt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.attempt.dto.QuizAttemptDto;
import org.example.mizzyquiz.attempt.repository.QuizAttemptRepository;
import org.example.mizzyquiz.auth.entity.User;
import org.example.mizzyquiz.auth.repository.UserRepository;
import org.example.mizzyquiz.quiz.enitity.QuizAttempt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/v1/attempt")
@RequiredArgsConstructor
@Slf4j
public class QuizAttemptController {

    private final UserRepository userRepository;
    private final QuizAttemptRepository attemptRepository;


    @GetMapping("/myattempts/")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<QuizAttemptDto>> getMyAttempts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<QuizAttempt> attempts = attemptRepository.findByUserId(user.getId());

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
    }
}
