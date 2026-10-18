package com.codeplatform.code_executor.service;

import com.codeplatform.code_executor.entity.Submission;
import com.codeplatform.code_executor.repository.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CodeExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(CodeExecutionService.class);

    @Autowired
    private S3Service s3Service;

    @Autowired
    private SubmissionRepository submissionRepository;

    public List<Submission> getHistoryByUserId(Long userId) {
        logger.info("Fetching history for userId: {}", userId);
        List<Submission> submissions = submissionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        logger.info("Repository returned {} submissions for userId: {}", submissions.size(), userId);
        
        // Log each submission's userId for debugging
        submissions.forEach(sub -> logger.info("Submission id={}, userId={}", sub.getId(), sub.getUserId()));
        
        return submissions;
    }

    public String getCodeById(Long id, Long userId) throws IOException {
        logger.info("Fetching code for submission id: {}, userId: {}", id, userId);

        Submission submission = submissionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IOException("Submission not found or access denied"));

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
