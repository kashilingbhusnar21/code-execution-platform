package com.codeplatform.code_executor.entity;

public enum SubmissionStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    ACCEPTED,
    WRONG_ANSWER,
    COMPILE_ERROR,
    COMPILATION_ERROR,
    RUNTIME_ERROR,
    TIME_LIMIT_EXCEEDED,
    TIMEOUT,
    ERROR
}
