package org.example.mizzyquiz.admin.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreateDto {
    @NotBlank(message = "Question text is required")
    @Size(max = 1000)
    private String text;

    @Min(1)
    private Integer points;

    @Size(max = 2000)
    private String explanation;

    @NotEmpty(message = "Options are required")
    @Size(min = 2, message = "At least 2 options are required")
    private List<OptionDto> options;
}
