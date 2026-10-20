package com.codeplatform.code_executor.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicStatusUpdateRequest {
    private String status;
    private Integer quizScore;
}
