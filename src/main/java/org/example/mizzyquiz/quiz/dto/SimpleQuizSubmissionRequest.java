package org.example.mizzyquiz.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleQuizSubmissionRequest {

    @NotEmpty(message = "Answers cannot be empty")
    @Valid
    private List<SimpleAnswerRequest> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleAnswerRequest {

        @Min(value = 0, message = "Question index must be 0 or greater")
        private Integer questionIndex;  //Hello verto team, you can put  0, 1, 2 you can put

        @Min(value = 0, message = "Option index must be 0 or greater")
        private Integer optionIndex;
    }
}
