package org.example.mizzyquiz.quiz.enitity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OptionId implements Serializable {

    @Column(name = "question_id")
    private UUID questionId;

    @Column(name = "order_index")
    private Integer orderIndex;
}
