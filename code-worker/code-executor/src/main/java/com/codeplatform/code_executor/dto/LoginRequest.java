package com.codeplatform.code_executor.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
