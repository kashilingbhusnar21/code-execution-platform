package com.codeplatform.code_executor.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTrackProgressDTO {
    private String trackSlug;
    private String trackTitle;
    private int totalTopics;
    private int completedTopics;
    private int inProgressTopics;
    private double completionPercentage;
    private String lastActiveTopicSlug;

    @Builder.Default
    private Map<String, String> topicStatuses = new HashMap<>();
}
