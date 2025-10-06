package org.example.mizzyquiz.quiz.enitity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "questions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 1000)
    private String text;

    @Column(length = 2000)
    private String explanation;

    @Column(nullable = false)
    @Builder.Default
    private Integer points = 1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // One-to-Many with Options (Options belong to specific questions)
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Option> options = new ArrayList<>();


    // Helper methods
    public void addOption(Option option) {
        options.add(option);
        option.setQuestion(this);
    }


    public List<Option> getCorrectOptions() {
        return options.stream()
                .filter(Option::isCorrect)
                .toList();
    }

    public boolean isValidConfiguration() {
        if (options.isEmpty()) return false;

        long correctCount = options.stream().filter(Option::isCorrect).count();
        return correctCount==1;

    }
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private QuestionType type = QuestionType.SINGLE_CHOICE;

    @PrePersist
    @PreUpdate
    public void validateQuestion() {


        long correctCount = options.stream().filter(Option::isCorrect).count();

        // Valideting based on question type
        switch (type) {
            case SINGLE_CHOICE:
                if (correctCount != 1) {
                    throw new IllegalStateException(
                            "Single choice question must have exactly 1 correct answer, found: " + correctCount
                    );
                }
                if (options.size() < 2) {
                    throw new IllegalStateException("Single choice question must have at least 2 options");
                }
                break;

            case MULTIPLE_CHOICE:
                if (correctCount < 1) {
                    throw new IllegalStateException("Multiple choice question must have at least 1 correct answer");
                }
                if (options.size() < 2) {
                    throw new IllegalStateException("Multiple choice question must have at least 2 options");
                }
                break;

            case TEXT:
                if (text.length() > 300) {
                    throw new IllegalStateException("Text question cannot exceed 300 characters");
                }

                break;
        }
    }
}
