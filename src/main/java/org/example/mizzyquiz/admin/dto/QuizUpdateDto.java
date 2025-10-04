package org.example.mizzyquiz.admin.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizUpdateDto {
    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    private Integer timeLimitMinutes;
    private Integer passingScore;
    private Integer maxAttempts;
}
