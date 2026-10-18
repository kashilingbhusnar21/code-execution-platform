package com.codeplatform.code_executor.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CodeResponse {

    private Long id;
    private String language;
    private String output;
    private String status;
    private LocalDateTime createdAt;
    private Long executionTime;
    private Long problemId;
    private Integer passedCount;
    private Integer totalCount;
    private String errorDetails;
    private List<TestCaseResultItem> testCaseResults;

    public CodeResponse() {
    }

    public CodeResponse(Long id, String language, String output, String status, LocalDateTime createdAt) {
        this(id, language, output, status, createdAt, null);
    }

    public CodeResponse(Long id, String language, String output, String status,
                        LocalDateTime createdAt, Long executionTime) {
        this.id = id;
        this.language = language;
        this.output = output;
        this.status = status;
        this.createdAt = createdAt;
        this.executionTime = executionTime;
    }

    public CodeResponse(Long id, String language, String output, String status,
                        LocalDateTime createdAt, Long executionTime, Long problemId,
                        Integer passedCount, Integer totalCount, String errorDetails,
                        List<TestCaseResultItem> testCaseResults) {
        this.id = id;
        this.language = language;
        this.output = output;
        this.status = status;
        this.createdAt = createdAt;
        this.executionTime = executionTime;
        this.problemId = problemId;
        this.passedCount = passedCount;
        this.totalCount = totalCount;
        this.errorDetails = errorDetails;
        this.testCaseResults = testCaseResults;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public Integer getPassedCount() {
        return passedCount;
    }

    public void setPassedCount(Integer passedCount) {
        this.passedCount = passedCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public List<TestCaseResultItem> getTestCaseResults() {
        return testCaseResults;
    }

    public void setTestCaseResults(List<TestCaseResultItem> testCaseResults) {
        this.testCaseResults = testCaseResults;
    }
}
