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
public class UserTopicProgressDTO {
    private String topicSlug;
    private String topicTitle;
    private String status;
    private Integer quizScore;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
}
