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


    // Many-to-Many back reference to Quiz
    @ManyToMany(mappedBy = "questions")
    private Set<Quiz> quizzes = new HashSet<>();

    // One-to-Many with Options (Options belong to specific questions)
    @OneToMany(cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();


    // Helper methods
    public void addOption(Option option) {
        options.add(option);
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
}
