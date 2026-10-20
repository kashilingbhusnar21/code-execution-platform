package com.codeplatform.code_executor.learning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_topics", indexes = {
        @Index(name = "idx_learning_topics_slug", columnList = "slug"),
        @Index(name = "idx_learning_topics_phase_order", columnList = "phase_id, sequence_order")
})
@Getter
@Setter
public class LearningTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "phase_id", nullable = false)
    private RoadmapPhase phase;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @Column(name = "slug", nullable = false, unique = true, length = 120)
    private String slug;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "content_path", nullable = false, length = 255)
    private String contentPath;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes = 45;

    @Column(name = "total_learning_hours")
    private Double totalLearningHours = 2.5;

    @Column(name = "tier", length = 30)
    private String tier = "CORE";

    @Column(name = "has_quiz", nullable = false)
    private Boolean hasQuiz = true;

    @Column(name = "has_project", nullable = false)
    private Boolean hasProject = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.hasQuiz == null) {
            this.hasQuiz = true;
        }
        if (this.hasProject == null) {
            this.hasProject = false;
        }
        if (this.estimatedMinutes == null) {
            this.estimatedMinutes = 45;
        }
        if (this.totalLearningHours == null) {
            this.totalLearningHours = 2.5;
        }
        if (this.tier == null) {
            this.tier = "CORE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
