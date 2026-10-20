package com.codeplatform.code_executor.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningTopicDTO {
    private Long id;
    private Integer sequenceOrder;
    private String slug;
    private String title;
    private String summary;
    private String contentPath;
    private Integer estimatedMinutes;
    private Double totalLearningHours;
    private String tier;
    private Boolean hasQuiz;
    private Boolean hasProject;

    // User Progress (enriched when authenticated)
    private String status;
    private Integer quizScore;
    private LocalDateTime completedAt;
}
