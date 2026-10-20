package com.codeplatform.code_executor.learning.entity;

import com.codeplatform.code_executor.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_topic_progress",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_topic", columnNames = {"user_id", "topic_id"})
        },
        indexes = {
                @Index(name = "idx_user_progress_user", columnList = "user_id"),
                @Index(name = "idx_user_progress_status", columnList = "status")
        }
)
@Getter
@Setter
public class UserTopicProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private LearningTopic topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TopicProgressStatus status = TopicProgressStatus.NOT_STARTED;

    @Column(name = "quiz_score")
    private Integer quizScore;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TopicProgressStatus.NOT_STARTED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
