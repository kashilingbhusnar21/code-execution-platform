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
public class CareerPathDTO {
    private Long id;
    private String slug;
    private String title;
    private String description;
    private String icon;
    private Integer totalPhases;
    private Integer totalTopics;
    private Integer estimatedWeeks;
    private Boolean isActive;

    @Builder.Default
    private List<RoadmapPhaseDTO> phases = new ArrayList<>();

    private UserTrackProgressDTO userProgress;
}
