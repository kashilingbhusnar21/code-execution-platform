package com.codeplatform.code_executor.dto;

import java.time.LocalDateTime;

public class CodeResponse {

    private Long id;
    private String language;
    private String output;
    private String status;
    private LocalDateTime createdAt;
    private Long executionTime;

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
}
