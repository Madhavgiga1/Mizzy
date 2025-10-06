package org.example.mizzyquiz.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mizzyquiz.admin.dto.OptionDto;
import org.example.mizzyquiz.admin.dto.QuestionCreateDto;

import org.example.mizzyquiz.exception.ResourceNotFoundException;
import org.example.mizzyquiz.quiz.enitity.Option;
import org.example.mizzyquiz.quiz.enitity.Question;
import org.apache.coyote.BadRequestException;
import org.example.mizzyquiz.quiz.enitity.QuestionType;
import org.example.mizzyquiz.quiz.enitity.Quiz;
import org.example.mizzyquiz.quiz.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionManagementService {

    private final QuestionRepository questionRepository;

    @Transactional
    public Question createQuestion(QuestionCreateDto dto) throws BadRequestException {
        // Validate
        if (dto.getText().length() > 1000) {
            throw new BadRequestException("Question text too long (max 1000 characters)");
        }

        Question question = Question.builder()
                .text(dto.getText())
                .points(dto.getPoints() != null ? dto.getPoints() : 1)
                .explanation(dto.getExplanation())
                .build();



        // Add options
        for (OptionDto optionDto : dto.getOptions()) {
            Option option = Option.builder()
                    .text(optionDto.getText())
                    .correct(optionDto.isCorrect())
                    .build();
            question.addOption(option);
        }

        // Validate question configuration
        question.validateQuestion();

        question = questionRepository.save(question);
        log.info("Question created: {}", question.getId());

        return question;
    }

    @Transactional
    public Question updateQuestion(UUID questionId, QuestionCreateDto dto) throws BadRequestException {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Check if question is part of published quiz


        question.setText(dto.getText());
        question.setPoints(dto.getPoints());
        question.setExplanation(dto.getExplanation());


        // Update options
        question.getOptions().clear();
        for (OptionDto optionDto : dto.getOptions()) {
            Option option = Option.builder()
                    .text(optionDto.getText())
                    .correct(optionDto.isCorrect())
                    .build();
            question.addOption(option);
        }

        question.validateQuestion();

        return questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(UUID questionId) throws BadRequestException {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Check if question is part of any quiz


        questionRepository.delete(question);
        log.info("Question deleted: {}", questionId);
    }

    public Page<Question> searchQuestions(String search, String category,
                                          QuestionType type, Pageable pageable) {
        // Implement search logic based on parameters
        if (search != null && !search.isEmpty()) {
            return questionRepository.searchQuestions(search, pageable);
        }
        return questionRepository.findAll(pageable);
    }


}
