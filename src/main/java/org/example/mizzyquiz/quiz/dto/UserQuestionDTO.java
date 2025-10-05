package org.example.mizzyquiz.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserQuestionDTO {
    private UUID id;
    private String text;
    private String explanation;
    private Integer points;
    private List<OptionDTO> options;
}
