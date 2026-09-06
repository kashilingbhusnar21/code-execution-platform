package com.codeplatform.code_executor.dto;

import com.codeplatform.code_executor.entity.SubmissionStatus;

public class RunResponse {

    private String output;
    private String stdout;
    private String stderr;
    private String status;
    private Long executionTimeMs;

    public RunResponse() {
    }

    public RunResponse(String output, String stdout, String stderr, String status, Long executionTimeMs) {
        this.output = output;
        this.stdout = stdout;
        this.stderr = stderr;
        this.status = status;
        this.executionTimeMs = executionTimeMs;
    }

    public static RunResponse from(String output, SubmissionStatus status, long executionTimeMs) {
        String stdout = "";
        String stderr = "";

        if (status == SubmissionStatus.SUCCESS) {
            stdout = output == null ? "" : output;
        } else {
            stderr = output == null ? "" : output;
        }

        return new RunResponse(
                output == null ? "" : output,
                stdout,
                stderr,
                status == null ? "ERROR" : status.name(),
                executionTimeMs
        );
    }

    public static RunResponse error(String message) {
        return new RunResponse(message, "", message, "ERROR", 0L);
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
}
