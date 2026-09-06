package com.codeplatform.code_executor.service;

import com.codeplatform.code_executor.dto.RunResponse;
import com.codeplatform.code_executor.entity.Submission;
import com.codeplatform.code_executor.entity.SubmissionStatus;
import com.codeplatform.code_executor.executor.CodeExecutor;
import com.codeplatform.code_executor.executor.ExecutorRegistry;
import com.codeplatform.code_executor.repository.SubmissionRepository;
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
import java.util.List;
import java.util.Optional;

@Service
public class CodeExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(CodeExecutionService.class);
    private static final Path BASE_FOLDER = ContainerManagerService.HOST_TEMP_DIR;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ExecutorRegistry executorRegistry;

    @Autowired
    private ContainerManagerService containerManager;

    public RunResponse executeCode(String code, String language, String userId, String input) {
        logger.info("Executing code: language={}, userId={}", language, userId);

        Optional<CodeExecutor> executorOpt = executorRegistry.find(language);
        if (executorOpt.isEmpty()) {
            logger.error("No executor found for language: {}. Supported: {}",
                    language, executorRegistry.getSupportedLanguages());
            return RunResponse.error(
                    "Error: Language '" + language + "' is not supported. Supported: "
                            + executorRegistry.getSupportedLanguages());
        }

        // Ensure warm container is healthy before writing files / executing
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
        String s3Key = null;

        try {
            Path tempDir = BASE_FOLDER.resolve(uniqueFolder);
            Files.createDirectories(tempDir);
            Files.write(Paths.get(filePath), code.getBytes(StandardCharsets.UTF_8));
            logger.info("Code saved to temp file: {}", filePath);

            s3Key = s3Service.uploadFile(filePath, userId, fileName);
            logger.info("File uploaded to S3 with key: {}", s3Key);

            CodeExecutor.ExecutionResult result = executor.execute(code, filePath, input);

            Submission submission = new Submission(
                    userId,
                    language.toLowerCase(),
                    s3Key,
                    result.getOutput(),
                    result.getStatus(),
                    result.getExecutionTimeMs()
            );
            submissionRepository.save(submission);
            logger.info("Submission saved id={}, status={}, time={}ms",
                    submission.getId(), result.getStatus(), result.getExecutionTimeMs());

            return RunResponse.from(result.getOutput(), result.getStatus(), result.getExecutionTimeMs());

        } catch (IOException e) {
            logger.error("IO error during code execution", e);
            String message = "Error executing code: " + e.getMessage();

            try {
                Submission submission = new Submission(
                        userId,
                        language.toLowerCase(),
                        s3Key != null ? s3Key : userId + "_" + timestamp + "_" + fileName,
                        message,
                        SubmissionStatus.RUNTIME_ERROR,
                        0L
                );
                submissionRepository.save(submission);
            } catch (Exception dbEx) {
                logger.error("Failed to save error submission to database", dbEx);
            }

            return RunResponse.from(message, SubmissionStatus.RUNTIME_ERROR, 0L);
        }
    }

    public List<Submission> getHistoryByUserId(String userId) {
        logger.info("Fetching history for userId: {}", userId);
        return submissionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public String getCodeById(Long id) throws IOException {
        logger.info("Fetching code for submission id: {}", id);

        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new IOException("Submission not found with id: " + id));

        if (submission.getS3Key() != null) {
            String downloadedPath = s3Service.downloadFile(submission.getS3Key());
            Path path = Paths.get(downloadedPath);
            String code = Files.readString(path);
            Files.deleteIfExists(path);
            return code;
        }

        throw new IOException("No code found for submission id: " + id);
    }
}
