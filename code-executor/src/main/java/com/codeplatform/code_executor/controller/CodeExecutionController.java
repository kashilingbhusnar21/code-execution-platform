package com.codeplatform.code_executor.controller;

import com.codeplatform.code_executor.dto.CodeResponse;
import com.codeplatform.code_executor.dto.CodeSubmissionMessage;
import com.codeplatform.code_executor.dto.RunResponse;
import com.codeplatform.code_executor.entity.Submission;
import com.codeplatform.code_executor.entity.SubmissionStatus;
import com.codeplatform.code_executor.model.CodeRequest;
import com.codeplatform.code_executor.service.CodeExecutionService;
import com.codeplatform.code_executor.service.RabbitMQProducerService;
import com.codeplatform.code_executor.repository.SubmissionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/code")
public class CodeExecutionController {

    private static final Logger logger = LoggerFactory.getLogger(CodeExecutionController.class);

    @Autowired
    private CodeExecutionService codeExecutionService;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;

    @Autowired
    private SubmissionRepository submissionRepository;

    @PostMapping("/run")
    public ResponseEntity<RunResponse> executeCode(@RequestBody CodeRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");

        logger.info("Received code execution request: language={}, userId={}",
                request.getLanguage(), userId);

        if (request.getCode() == null || request.getCode().isEmpty()) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: code is required"));
        }

        if (request.getLanguage() == null || request.getLanguage().isEmpty()) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: language is required"));
        }

        if (userId == null) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: userId is required"));
        }

        // Create submission record with PENDING status
        Submission submission = new Submission(
                userId,
                request.getLanguage().toLowerCase(),
                "pending_" + userId + "_" + Instant.now().toEpochMilli(),
                null,
                SubmissionStatus.PENDING,
                null
        );
        submissionRepository.save(submission);
        logger.info("Submission created with PENDING status: id={}", submission.getId());

        // Send to RabbitMQ for async processing
        CodeSubmissionMessage message = new CodeSubmissionMessage(
                submission.getId(),
                request.getLanguage(),
                request.getCode(),
                request.getInput(),
                userId
        );
        rabbitMQProducerService.sendCodeSubmission(message);

        // Return immediate response with submission ID
        RunResponse response = new RunResponse();
        response.setOutput("Submission queued for execution. Use the submission ID to check status.");
        response.setStatus("PENDING");
        response.setExecutionTimeMs(0L);
        response.setSubmissionId(submission.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<CodeResponse>> getHistory(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        logger.info("Received history request - userId from JWT: {}", userId);

        if (userId == null) {
            logger.error("userId is null - JWT filter may not be working");
            return ResponseEntity.badRequest().build();
        }

        List<Submission> submissions = codeExecutionService.getHistoryByUserId(userId);
        logger.info("Found {} submissions for userId: {}", submissions.size(), userId);

        List<CodeResponse> responses = submissions.stream()
                .map(sub -> new CodeResponse(
                        sub.getId(),
                        sub.getLanguage(),
                        sub.getOutput(),
                        sub.getStatus().name(),
                        sub.getCreatedAt(),
                        sub.getExecutionTime()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getCodeById(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        logger.info("Received code retrieval request for id: {}, userId: {}", id, userId);

        try {
            String code = codeExecutionService.getCodeById(id, userId);
            return ResponseEntity.ok(code);
        } catch (Exception e) {
            logger.error("Error retrieving code: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<CodeResponse> getSubmissionStatus(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        logger.info("Received status check request for id: {}, userId: {}", id, userId);

        Submission submission = submissionRepository.findByIdAndUserId(id, userId).orElse(null);
        if (submission == null) {
            return ResponseEntity.notFound().build();
        }

        CodeResponse response = new CodeResponse(
                submission.getId(),
                submission.getLanguage(),
                submission.getOutput(),
                submission.getStatus().name(),
                submission.getCreatedAt(),
                submission.getExecutionTime()
        );

        return ResponseEntity.ok(response);
    }
}
