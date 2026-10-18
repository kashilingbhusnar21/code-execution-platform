package com.codeplatform.code_executor.dto;

import com.codeplatform.code_executor.entity.Problem.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemCreateRequest {
    private String title;
    private String description;
    private Difficulty difficulty;
    private Integer timeLimit;
    private Integer memoryLimit;
    private String constraints;
    private String starterCode;
    private Set<String> topics;
    private List<TestCaseCreateRequest> testCases;
}
