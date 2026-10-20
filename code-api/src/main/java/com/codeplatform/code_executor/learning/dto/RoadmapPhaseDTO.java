package com.codeplatform.code_executor.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapPhaseDTO {
    private Long id;
    private Integer phaseNumber;
    private String title;
    private String description;
    private Integer estimatedHours;
    private String color;

    @Builder.Default
    private List<LearningTopicDTO> topics = new ArrayList<>();

    private Integer totalTopics;
    private Integer completedTopics;
}
