package com.codeplatform.code_executor.dto;

import java.io.Serializable;

public class CodeResultMessage implements Serializable {

    private Long submissionId;
    private String output;
    private String stdout;
    private String stderr;
    private String status;
    private Long executionTimeMs;
    private String errorMessage;

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
}
