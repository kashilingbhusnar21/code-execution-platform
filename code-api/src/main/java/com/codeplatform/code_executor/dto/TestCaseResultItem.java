package com.codeplatform.code_executor.dto;

import java.io.Serializable;

public class TestCaseResultItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private int testCaseNumber;
    private boolean passed;
    private String status; // PASSED, FAILED, TIMEOUT, ERROR, COMPILE_ERROR, RUNTIME_ERROR
    private Long executionTimeMs;
    private Boolean isSample;
    private Boolean isHidden;
    private String input;          // ONLY populated for public sample test cases
    private String expectedOutput;  // ONLY populated for public sample test cases
    private String actualOutput;    // ONLY populated for public sample test cases
    private String failureReason;

    public TestCaseResultItem() {
    }

    public TestCaseResultItem(Long id, int testCaseNumber, boolean passed, String status, Long executionTimeMs,
                              Boolean isSample, Boolean isHidden, String input, String expectedOutput,
                              String actualOutput, String failureReason) {
        this.id = id;
        this.testCaseNumber = testCaseNumber;
        this.passed = passed;
        this.status = status;
        this.executionTimeMs = executionTimeMs;
        this.isSample = isSample;
        this.isHidden = isHidden;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.actualOutput = actualOutput;
        this.failureReason = failureReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTestCaseNumber() {
        return testCaseNumber;
    }

    public void setTestCaseNumber(int testCaseNumber) {
        this.testCaseNumber = testCaseNumber;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
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

    public Boolean getIsSample() {
        return isSample;
    }

    public void setIsSample(Boolean isSample) {
        this.isSample = isSample;
    }

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public String getActualOutput() {
        return actualOutput;
    }

    public void setActualOutput(String actualOutput) {
        this.actualOutput = actualOutput;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
