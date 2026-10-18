package com.codeplatform.code_executor.dto;

import java.io.Serializable;
import java.util.List;

public class CodeSubmissionMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long submissionId;
    private String language;
    private String code;
    private String input;
    private Long userId;
    private Long problemId;
    private String problemTitle;
    private String mode; // "RUN" or "SUBMIT"
    private List<TestCaseItem> testCases;

    public CodeSubmissionMessage() {
    }

    public CodeSubmissionMessage(Long submissionId, String language, String code, String input, Long userId) {
        this.submissionId = submissionId;
        this.language = language;
        this.code = code;
        this.input = input;
        this.userId = userId;
        this.mode = "RUN";
    }

    public CodeSubmissionMessage(Long submissionId, String language, String code, String input, Long userId, Long problemId, String problemTitle) {
        this.submissionId = submissionId;
        this.language = language;
        this.code = code;
        this.input = input;
        this.userId = userId;
        this.problemId = problemId;
        this.problemTitle = problemTitle;
        this.mode = "RUN";
    }

    public CodeSubmissionMessage(Long submissionId, String language, String code, String input, Long userId,
                                 Long problemId, String problemTitle, String mode, List<TestCaseItem> testCases) {
        this.submissionId = submissionId;
        this.language = language;
        this.code = code;
        this.input = input;
        this.userId = userId;
        this.problemId = problemId;
        this.problemTitle = problemTitle;
        this.mode = mode;
        this.testCases = testCases;
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

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getProblemTitle() {
        return problemTitle;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<TestCaseItem> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCaseItem> testCases) {
        this.testCases = testCases;
    }
}
