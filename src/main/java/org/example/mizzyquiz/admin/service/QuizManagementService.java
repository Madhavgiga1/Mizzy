package org.example.mizzyquiz.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.admin.dto.QuizCreateDto;
import org.example.mizzyquiz.admin.dto.QuizUpdateDto;
import org.example.mizzyquiz.auth.entity.User;
import org.example.mizzyquiz.exception.BadRequestException;
import org.example.mizzyquiz.exception.ResourceNotFoundException;
import org.example.mizzyquiz.quiz.dto.QuizDto;
import org.example.mizzyquiz.quiz.enitity.Option;
import org.example.mizzyquiz.quiz.enitity.Question;
import org.example.mizzyquiz.quiz.enitity.Quiz;
import org.example.mizzyquiz.quiz.repository.QuestionRepository;
import org.example.mizzyquiz.quiz.repository.QuizRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizManagementService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public QuizDto createQuiz(QuizCreateDto dto, User admin) {
        Quiz quiz = Quiz.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .createdBy(admin)
                .timeLimitMinutes(dto.getTimeLimitMinutes())
                .passingScore(dto.getPassingScore())
                .maxAttempts(dto.getMaxAttempts() != null ? dto.getMaxAttempts() : 1)
                .published(false)
                .build();

        quiz = quizRepository.save(quiz);
        log.info("Quiz created: {} by admin: {}", quiz.getId(), admin.getUsername());

        return mapToDto(quiz);
    }

    @Transactional
    public QuizDto updateQuiz(Long quizId, QuizUpdateDto dto) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (quiz.isPublished()) {
            throw new BadRequestException("Cannot update published quiz");
        }

        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setTimeLimitMinutes(dto.getTimeLimitMinutes());
        quiz.setPassingScore(dto.getPassingScore());
        quiz.setMaxAttempts(dto.getMaxAttempts());

        quiz = quizRepository.save(quiz);
        return mapToDto(quiz);
    }

    @Transactional
    public void addQuestionsToQuiz(Long quizId, List<Question> questions) {

        Quiz quiz=quizRepository.findById(quizId).get();


        for (Question question : questions) {
            question.setQuiz(quiz);
            quiz.addQuestion(question);
            for (Option option : question.getOptions()) {
                option.setQuestion(question);
            }
            quiz.addQuestion(question);
        }

        quizRepository.save(quiz);
        log.info("Added {} questions to quiz {}", questions.size(), quizId);
    }

    @Transactional
    public QuizDto publishQuiz(Long quizId) {

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new BadRequestException("Quiz not found"));

        for (Question question : quiz.getQuestions()) {
            question.validateQuestion();
        }

        quiz.setPublished(true);
        quiz = quizRepository.save(quiz);
        log.info("Done: {}", quizId);

        return mapToDto(quiz);
    }

    @Transactional
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (quiz.isPublished() && !quiz.getAttempts().isEmpty()) {
            throw new BadRequestException("Cannot delete quiz with attempts");
        }

        quizRepository.delete(quiz);
        log.info("Quiz deleted: {}", quizId);
    }

    public Page<QuizDto> getAdminQuizzes(User admin, Pageable pageable) {
        return quizRepository.findByCreatedBy(admin, pageable)
                .map(this::mapToDto);
    }

    private QuizDto mapToDto(Quiz quiz) {
        return QuizDto.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .questionCount(quiz.getQuestionCount())
                .totalPoints(quiz.getTotalPoints())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .passingScore(quiz.getPassingScore())
                .maxAttempts(quiz.getMaxAttempts())
                .published(quiz.isPublished())
                .createdAt(quiz.getCreatedAt())
                .build();
    }
}
