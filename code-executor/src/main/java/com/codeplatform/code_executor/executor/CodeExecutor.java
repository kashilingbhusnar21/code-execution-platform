package com.codeplatform.code_executor.executor;

import com.codeplatform.code_executor.entity.SubmissionStatus;

/**
 * Language-specific code runner (Strategy).
 *
 * Register by annotating the implementation with {@code @Component}.
 * Lookup is always by {@link #getLanguage()} via {@link ExecutorRegistry}
 * — never inject {@code Map<String, CodeExecutor>} (that uses Spring bean names).
 */
public interface CodeExecutor {

    /**
     * Executes code and returns the output along with execution status.
     *
     * @param code     source code (already written to filePath by the service)
     * @param filePath absolute path to the source file inside a unique work folder
     * @param input    optional stdin (can be null/blank)
     */
    ExecutionResult execute(String code, String filePath, String input);

    /**
     * Language id used by the API and frontend (e.g. "java", "python", "cpp").
     * Must be unique across all executors. Case is ignored.
     */
    String getLanguage();

    /**
     * Source file name for this language (e.g. Main.java, main.py, main.cpp).
     * Keeps language-specific naming out of {@code CodeExecutionService}.
     */
    String getSourceFileName();

    class ExecutionResult {
        private final String output;
        private final SubmissionStatus status;
        private final long executionTimeMs;

        public ExecutionResult(String output, SubmissionStatus status, long executionTimeMs) {
            this.output = output;
            this.status = status;
            this.executionTimeMs = executionTimeMs;
        }

        public String getOutput() {
            return output;
        }

        public SubmissionStatus getStatus() {
            return status;
        }

        public long getExecutionTimeMs() {
            return executionTimeMs;
        }
    }
}
