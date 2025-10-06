package org.example.mizzyquiz.quiz.enitity;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.*;
import org.example.mizzyquiz.auth.entity.User;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Quiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    // One Quiz has Many Questions (Owning side is Question)
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private Set<QuizAttempt> attempts = new HashSet<>();

    // explicit no-arg constructor to ensure collections are initialized
    public Quiz() {
        this.questions = new ArrayList<>();
        this.attempts = new HashSet<>();
        this.published = false;
        this.maxAttempts = 1;
    }

    // lifecycle callbacks that JPA will call, questions field were throwing NPE before this
    @PostLoad
    @PostPersist
    @PostUpdate
    private void ensureCollections() {
        if (questions == null) questions = new ArrayList<>();
        if (attempts == null) attempts = new HashSet<>();
    }


    public void addQuestion(Question question) {
        if (questions == null) questions = new ArrayList<>();
        questions.add(question);
    }


    public int getTotalPoints() {
        return (questions == null) ? 0 : questions.stream().mapToInt(Question::getPoints).sum();
    }

    public int getQuestionCount() {
        return (questions == null) ? 0 : questions.size();
    }
}
