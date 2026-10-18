package com.codeplatform.code_executor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseDTO {
    private Long id;
    private String input;
    private String expectedOutput;
    private Boolean isSample;
    private Boolean isHidden;
    private String explanation;
}
