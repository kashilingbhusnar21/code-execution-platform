package com.codeplatform.code_executor.dto;

import com.codeplatform.code_executor.entity.Problem.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDTO {
    private Long id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private Integer timeLimit;
    private Integer memoryLimit;
    private String constraints;
    private String starterCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<TopicDTO> topics;
    private Set<TestCaseDTO> sampleTestCases;
}
