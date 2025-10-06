package org.example.mizzyquiz.quiz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.exception.QuizNotFoundException;
import org.example.mizzyquiz.exception.UnauthorizedException;
import org.example.mizzyquiz.quiz.dto.OptionDTO;
import org.example.mizzyquiz.quiz.dto.UserQuestionDTO;
import org.example.mizzyquiz.quiz.dto.QuizQuestionsResponse;
import org.example.mizzyquiz.quiz.enitity.Option;
import org.example.mizzyquiz.quiz.enitity.Question;
import org.example.mizzyquiz.quiz.enitity.Quiz;
import org.example.mizzyquiz.quiz.repository.QuizRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;


    @Transactional(readOnly = true)
    public QuizQuestionsResponse getQuizQuestions(Long quizId, boolean includeCorrectAnswers) {
        log.info("Fetching questions for quiz: {}, includeCorrectAnswers: {}", quizId, includeCorrectAnswers);

        // 1. Verify quiz exists
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + quizId));

        // 2. Check authorization for correct answers
        if (includeCorrectAnswers && !isAdmin()) {
            throw new UnauthorizedException("Only admins can view correct answers");
        }

        // 3. Get questions from quiz
        List<Question> questions = quiz.getQuestions();

        // 4. Map to DTO (hide correct answers for non-admins)
        return mapToResponse(quiz, questions, includeCorrectAnswers);
    }

    /**
     * Check if the current user has ADMIN role
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    /**
     * Map Quiz and Questions to Response DTO
     */
    private QuizQuestionsResponse mapToResponse(Quiz quiz, List<Question> questions, boolean includeCorrectAnswers) {
        List<UserQuestionDTO> questionDTOs = questions.stream()
                .map(question -> mapToQuestionDTO(question, includeCorrectAnswers))
                .collect(Collectors.toList());

        return QuizQuestionsResponse.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .description(quiz.getDescription())
                .totalPoints(quiz.getTotalPoints())
                .questionCount(quiz.getQuestionCount())
                .questions(questionDTOs)
                .build();
    }

    /**
     * Map Question entity to QuestionDTO
     */
    private UserQuestionDTO mapToQuestionDTO(Question question, boolean includeCorrectAnswers) {
        List<OptionDTO> optionDTOs = question.getOptions().stream()
                .map(option -> mapToOptionDTO(option, includeCorrectAnswers))
                .collect(Collectors.toList());

        return UserQuestionDTO.builder()
                .id(question.getId())
                .text(question.getText())
                .explanation(includeCorrectAnswers ? question.getExplanation() : null)
                .points(question.getPoints())
                .options(optionDTOs)
                .build();
    }

    /**
     * Map Option entity to OptionDTO
     * For regular users: correct is always null
     * For admins: correct is true/false
     */
    private OptionDTO mapToOptionDTO(Option option, boolean includeCorrectAnswers) {
        return OptionDTO.builder()
                .id(option.getId())
                .text(option.getText())
                .correct(includeCorrectAnswers ? option.isCorrect() : null)
                .build();
    }
}
