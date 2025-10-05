package org.example.mizzyquiz.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OptionDTO {
    private UUID id;
    private String text;
    private Boolean correct; // null for regular users, true/false for admins
}
