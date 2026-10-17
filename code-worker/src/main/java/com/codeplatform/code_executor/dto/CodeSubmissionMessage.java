package com.codeplatform.code_executor.dto;

import java.io.Serializable;

public class CodeSubmissionMessage implements Serializable {

    private Long submissionId;
    private String language;
    private String code;
    private String input;
    private Long userId;

    public CodeSubmissionMessage() {
    }

    public CodeSubmissionMessage(Long submissionId, String language, String code, String input, Long userId) {
        this.submissionId = submissionId;
        this.language = language;
        this.code = code;
        this.input = input;
        this.userId = userId;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
