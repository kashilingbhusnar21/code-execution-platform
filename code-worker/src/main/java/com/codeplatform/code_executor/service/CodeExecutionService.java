package com.codeplatform.code_executor.service;

import com.codeplatform.code_executor.dto.RunResponse;
import com.codeplatform.code_executor.entity.SubmissionStatus;
import com.codeplatform.code_executor.executor.CodeExecutor;
import com.codeplatform.code_executor.executor.ExecutorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;

@Service
public class CodeExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(CodeExecutionService.class);
    private static final Path BASE_FOLDER = ContainerManagerService.HOST_TEMP_DIR;

    @Autowired
    private ExecutorRegistry executorRegistry;

    @Autowired
    private ContainerManagerService containerManager;

    public RunResponse executeCodeOnly(String code, String language, Long userId, String input) {
        logger.info("Executing code only (no DB save): language={}, userId={}", language, userId);

        Optional<CodeExecutor> executorOpt = executorRegistry.find(language);
        if (executorOpt.isEmpty()) {
            logger.error("No executor found for language: {}. Supported: {}",
                    language, executorRegistry.getSupportedLanguages());
            return RunResponse.error(
                    "Error: Language '" + language + "' is not supported. Supported: "
                            + executorRegistry.getSupportedLanguages());
        }

        try {
            containerManager.ensureContainerRunning(language);
        } catch (Exception e) {
            logger.error("Warm container unavailable for {}: {}", language, e.getMessage());
            return RunResponse.error(
                    "Error: execution environment for '" + language + "' is unavailable: " + e.getMessage());
        }

        CodeExecutor executor = executorOpt.get();
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String uniqueFolder = userId + "_" + timestamp;
        String fileName = executor.getSourceFileName();
        String filePath = BASE_FOLDER.resolve(uniqueFolder).resolve(fileName).toString();

        try {
            Path tempDir = BASE_FOLDER.resolve(uniqueFolder);
            Files.createDirectories(tempDir);
            Files.write(Paths.get(filePath), code.getBytes(StandardCharsets.UTF_8));
            logger.info("Code saved to temp file: {}", filePath);

            CodeExecutor.ExecutionResult result = executor.execute(code, filePath, input);
            logger.info("Code execution completed: status={}, time={}ms",
                    result.getStatus(), result.getExecutionTimeMs());

            return RunResponse.from(result.getOutput(), result.getStatus(), result.getExecutionTimeMs());

        } catch (IOException e) {
            logger.error("IO error during code execution", e);
            return RunResponse.from("Error executing code: " + e.getMessage(), SubmissionStatus.RUNTIME_ERROR, 0L);
        }
    }
}
