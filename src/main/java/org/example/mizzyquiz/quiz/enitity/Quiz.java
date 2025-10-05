package org.example.mizzyquiz.quiz.enitity;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.*;
import org.example.mizzyquiz.auth.entity.User;

import java.util.*;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Quiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Builder.Default
    @Column(name = "is_published")
    private boolean published = false;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "max_attempts")
    @Builder.Default
    private Integer maxAttempts = 1;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @OrderColumn(name = "question_order")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private Set<QuizAttempt> attempts = new HashSet<>();

    // explicit no-arg constructor to ensure collections are initialized
    public Quiz() {
        this.questions = new ArrayList<>();
        this.attempts = new HashSet<>();
        this.published = false;
        this.maxAttempts = 1;
    }

    // lifecycle callbacks that JPA will call
    @PostLoad
    @PostPersist
    @PostUpdate
    private void ensureCollections() {
        if (questions == null) questions = new ArrayList<>();
        if (attempts == null) attempts = new HashSet<>();
    }

    // Helper methods (defensive)
    public void addQuestion(Question question) {
        if (questions == null) questions = new ArrayList<>();
        if (question.getQuizzes() == null) question.setQuizzes(new HashSet<>());
        questions.add(question);
        question.getQuizzes().add(this);
    }

    public void removeQuestion(Question question) {
        if (questions != null) {
            questions.remove(question);
        }
        if (question.getQuizzes() != null) {
            question.getQuizzes().remove(this);
        }
    }

    public int getTotalPoints() {
        return (questions == null) ? 0 : questions.stream().mapToInt(Question::getPoints).sum();
    }

    public int getQuestionCount() {
        return (questions == null) ? 0 : questions.size();
    }
}
