package org.example.mizzyquiz.admin.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {
    private UUID id;

    @NotBlank(message = "Option text is required")
    @Size(max = 500)
    private String text;

    private boolean correct;
    private String feedback;
}
