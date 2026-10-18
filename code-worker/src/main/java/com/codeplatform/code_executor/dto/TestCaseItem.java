package com.codeplatform.code_executor.dto;

import java.io.Serializable;

public class TestCaseItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String input;
    private String expectedOutput;
    private Boolean isSample;
    private Boolean isHidden;

    public TestCaseItem() {
    }

    public TestCaseItem(Long id, String input, String expectedOutput, Boolean isSample, Boolean isHidden) {
        this.id = id;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.isSample = isSample;
        this.isHidden = isHidden;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
