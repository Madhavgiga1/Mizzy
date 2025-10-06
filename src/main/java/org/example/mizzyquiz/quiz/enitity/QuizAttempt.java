package org.example.mizzyquiz.quiz.enitity;

import jakarta.persistence.*;
import lombok.*;
import org.example.mizzyquiz.auth.entity.User;

import java.time.LocalDateTime;

import java.util.UUID;

@Entity
@Table(name = "quiz_attempts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "is_passed")
    private Boolean passed;

    public void complete() {
        this.completedAt = LocalDateTime.now();
    }
}
