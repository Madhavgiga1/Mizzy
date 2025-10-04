package org.example.mizzyquiz.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mizzyquiz.admin.dto.OptionDto;
import org.example.mizzyquiz.quiz.enitity.QuestionType;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private UUID id;
    private String text;
    private QuestionType type;
    private Integer points;
    private boolean required;
    private List<OptionDto> options;
}
