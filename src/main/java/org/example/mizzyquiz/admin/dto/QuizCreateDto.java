package org.example.mizzyquiz.admin.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// QuizCreateDto.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizCreateDto {
    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    @Min(1)
    private Integer timeLimitMinutes;

    @Min(0)
    @Max(100)
    private Integer passingScore;

    @Min(1)
    private Integer maxAttempts;
}
