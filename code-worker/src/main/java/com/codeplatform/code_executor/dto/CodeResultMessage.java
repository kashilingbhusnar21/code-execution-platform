package com.codeplatform.code_executor.dto;

import java.io.Serializable;
import java.util.List;

public class CodeResultMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long submissionId;
    private String output;
    private String stdout;
    private String stderr;
    private String status;
    private Long executionTimeMs;
    private String errorMessage;
    private Integer passedCount;
    private Integer totalCount;
    private List<TestCaseResultItem> testCaseResults;

    public CodeResultMessage() {
    }

    public CodeResultMessage(Long submissionId, String output, String stdout, String stderr, 
                            String status, Long executionTimeMs, String errorMessage) {
        this.submissionId = submissionId;
        this.output = output;
        this.stdout = stdout;
        this.stderr = stderr;
        this.status = status;
        this.executionTimeMs = executionTimeMs;
        this.errorMessage = errorMessage;
    }

    public CodeResultMessage(Long submissionId, String output, String stdout, String stderr,
                            String status, Long executionTimeMs, String errorMessage,
                            Integer passedCount, Integer totalCount, List<TestCaseResultItem> testCaseResults) {
        this.submissionId = submissionId;
        this.output = output;
        this.stdout = stdout;
        this.stderr = stderr;
        this.status = status;
        this.executionTimeMs = executionTimeMs;
        this.errorMessage = errorMessage;
        this.passedCount = passedCount;
        this.totalCount = totalCount;
        this.testCaseResults = testCaseResults;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    public List<TestCaseResultItem> getTestCaseResults() {
        return testCaseResults;
    }

    public void setTestCaseResults(List<TestCaseResultItem> testCaseResults) {
        this.testCaseResults = testCaseResults;
    }
}
