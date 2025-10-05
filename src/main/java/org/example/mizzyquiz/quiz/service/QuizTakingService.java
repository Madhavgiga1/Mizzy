package org.example.mizzyquiz.quiz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.admin.dto.OptionDto;
import org.example.mizzyquiz.attempt.dto.QuizAttemptDto;
import org.example.mizzyquiz.attempt.repository.QuizAttemptRepository;
import org.example.mizzyquiz.auth.entity.User;
import org.example.mizzyquiz.exception.BadRequestException;
import org.example.mizzyquiz.exception.ResourceNotFoundException;
import org.example.mizzyquiz.quiz.dto.*;
import org.example.mizzyquiz.quiz.enitity.*;
import org.example.mizzyquiz.quiz.repository.QuizRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class QuizTakingService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final ScoringService scoringService;

    public Page<QuizDto> getAvailableQuizzes(Pageable pageable) {
        return quizRepository.findByPublishedTrue(pageable)
                .map(this::mapToDto);
    }

    public Page<QuizDto> searchQuizzes(String search, Pageable pageable) {
        return quizRepository.searchPublishedQuizzes(search, pageable)
                .map(this::mapToDto);
    }

    @Transactional
    public QuizAttemptDto startQuiz(Long quizId, User user) {
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (!quiz.isPublished()) {
            throw new BadRequestException("Quiz is not published");
        }

        // Check for existing in-progress attempt
        Optional<QuizAttempt> existingAttempt = attemptRepository
                .findByQuizAndUserAndStatus(quiz, user, AttemptStatus.IN_PROGRESS);

        if (existingAttempt.isPresent()) {
            QuizAttempt attempt = existingAttempt.get();
            if (!attempt.isExpired()) {
                return mapToAttemptDto(attempt);
            } else {
                // Mark as expired
                attempt.setStatus(AttemptStatus.EXPIRED);
                attemptRepository.save(attempt);
            }
        }

        // Check max attempts
        int userAttempts = quizRepository.countUserAttempts(quizId, user.getId());
        if (quiz.getMaxAttempts() != null && userAttempts >= quiz.getMaxAttempts()) {
            throw new BadRequestException("Maximum attempts reached for this quiz");
        }

        // Create new attempt
        QuizAttempt attempt = QuizAttempt.builder()
                .user(user)
                .quiz(quiz)
                .startedAt(LocalDateTime.now())
                .status(AttemptStatus.IN_PROGRESS)
                .totalScore(quiz.getTotalPoints())
                .build();

        attempt = attemptRepository.save(attempt);
        log.info("Quiz attempt started - User: {}, Quiz: {}", user.getUsername(), quizId);

        return mapToAttemptDto(attempt);
    }

    @Transactional(readOnly = true)
    public QuizPlayDto getQuizQuestions(UUID attemptId, User user) {
        QuizAttempt attempt = attemptRepository.findByIdAndUser(attemptId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("This quiz attempt is already completed");
        }

        if (attempt.isExpired()) {
            throw new BadRequestException("This quiz attempt has expired");
        }

        Quiz quiz = attempt.getQuiz();
        List<QuestionDto> questions = quiz.getQuestions().stream()
                .map(this::mapToQuestionDto)
                .collect(Collectors.toList());

        return QuizPlayDto.builder()
                .attemptId(attempt.getId())
                .quizTitle(quiz.getTitle())
                .description(quiz.getDescription())
                .questions(questions)
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .startedAt(attempt.getStartedAt())
                .totalQuestions(questions.size())
                .build();
    }

    @Transactional
    public AnswerResponseDto submitAnswer(UUID attemptId, SubmitAnswerDto dto, User user) {
        QuizAttempt attempt = attemptRepository.findByIdAndUser(attemptId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("Cannot submit answer for completed attempt");
        }

        if (attempt.isExpired()) {
            attempt.setStatus(AttemptStatus.EXPIRED);
            attemptRepository.save(attempt);
            throw new BadRequestException("Quiz attempt has expired");
        }

        // Find the question
        Question question = attempt.getQuiz().getQuestions().stream()
                .filter(q -> q.getId().equals(dto.getQuestionId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Question not found in quiz"));

        // Check if already answered
        Optional<Answer> existingAnswer = attempt.getAnswers().stream()
                .filter(a -> a.getQuestion().getId().equals(dto.getQuestionId()))
                .findFirst();

        Answer answer;
        if (existingAnswer.isPresent()) {
            answer = existingAnswer.get();
        } else {
            answer = Answer.builder()
                    .attempt(attempt)
                    .question(question)
                    .build();
        }

        // Set selected options based on question type


        // Don't evaluate correctness yet (only on final submission)
        attempt.addAnswer(answer);
        attemptRepository.save(attempt);

        return AnswerResponseDto.builder()
                .attemptId(attemptId)
                .questionId(dto.getQuestionId())
                .saved(true)
                .message("Answer saved successfully")
                .build();
    }

    @Transactional
    public QuizResultDto submitQuiz(UUID attemptId, User user) {
        QuizAttempt attempt = attemptRepository.findByIdAndUser(attemptId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("This quiz is already submitted");
        }

        // Calculate score
        QuizResultDto result = scoringService.calculateScore(attempt);

        // Update attempt
        attempt.setStatus(AttemptStatus.COMPLETED);
        attempt.setCompletedAt(LocalDateTime.now());
        attempt.setScore(result.getScore());
        attempt.setPercentage(result.getPercentage());
        attempt.setPassed(result.isPassed());

        attemptRepository.save(attempt);
        log.info("Quiz submitted - User: {}, Score: {}/{}",
                user.getUsername(), result.getScore(), result.getTotalScore());

        return result;
    }

    @Transactional(readOnly = true)
    public Page<QuizAttemptDto> getUserAttempts(User user, Pageable pageable) {
        return attemptRepository.findByUser(user, pageable)
                .map(this::mapToAttemptDto);
    }

    private QuizDto mapToDto(Quiz quiz) {
        return QuizDto.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .questionCount(quiz.getQuestionCount())
                .totalPoints(quiz.getTotalPoints())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .maxAttempts(quiz.getMaxAttempts())
                .build();
    }

    private QuizAttemptDto mapToAttemptDto(QuizAttempt attempt) {
        return QuizAttemptDto.builder()
                .id(attempt.getId())
                .quizId(attempt.getQuiz().getId())
                .quizTitle(attempt.getQuiz().getTitle())
                .startedAt(attempt.getStartedAt())
                .completedAt(attempt.getCompletedAt())
                .status(attempt.getStatus())
                .score(attempt.getScore())
                .totalScore(attempt.getTotalScore())
                .percentage(attempt.getPercentage())
                .passed(attempt.getPassed())
                .build();
    }

    private QuestionDto mapToQuestionDto(Question question) {
        List<OptionDto> options = question.getOptions().stream()
                .map(option -> OptionDto.builder()
                        .id(option.getId())
                        .text(option.getText())
                        // Don't include isCorrect for active quiz
                        .build())
                .collect(Collectors.toList());

        return QuestionDto.builder()
                .id(question.getId())
                .text(question.getText())
                .points(question.getPoints())
                .options(options)
                .build();
    }
}
