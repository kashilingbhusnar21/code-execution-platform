package com.codeplatform.code_executor.dto;

import com.codeplatform.code_executor.entity.SubmissionStatus;
import java.util.List;

public class GradingResult {
    private SubmissionStatus status;
    private int passedCount;
    private int totalCount;
    private long totalExecutionTimeMs;
    private String errorDetails;
    private List<TestCaseResultItem> testCaseResults;

    public GradingResult() {
    }

    public GradingResult(SubmissionStatus status, int passedCount, int totalCount,
                         long totalExecutionTimeMs, String errorDetails,
                         List<TestCaseResultItem> testCaseResults) {
        this.status = status;
        this.passedCount = passedCount;
        this.totalCount = totalCount;
        this.totalExecutionTimeMs = totalExecutionTimeMs;
        this.errorDetails = errorDetails;
        this.testCaseResults = testCaseResults;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public int getPassedCount() {
        return passedCount;
    }

    public void setPassedCount(int passedCount) {
        this.passedCount = passedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public long getTotalExecutionTimeMs() {
        return totalExecutionTimeMs;
    }

    public void setTotalExecutionTimeMs(long totalExecutionTimeMs) {
        this.totalExecutionTimeMs = totalExecutionTimeMs;
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
