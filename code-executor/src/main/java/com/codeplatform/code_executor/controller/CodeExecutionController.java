package com.codeplatform.code_executor.controller;

import com.codeplatform.code_executor.dto.CodeResponse;
import com.codeplatform.code_executor.dto.RunResponse;
import com.codeplatform.code_executor.entity.Submission;
import com.codeplatform.code_executor.model.CodeRequest;
import com.codeplatform.code_executor.service.CodeExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/code")
public class CodeExecutionController {

    private static final Logger logger = LoggerFactory.getLogger(CodeExecutionController.class);

    @Autowired
    private CodeExecutionService codeExecutionService;

    @PostMapping("/run")
    public ResponseEntity<RunResponse> executeCode(@RequestBody CodeRequest request) {
        logger.info("Received code execution request: language={}, userId={}",
                request.getLanguage(), request.getUserId());

        if (request.getCode() == null || request.getCode().isEmpty()) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: code is required"));
        }

        if (request.getLanguage() == null || request.getLanguage().isEmpty()) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: language is required"));
        }

        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: userId is required"));
        }

        RunResponse response = codeExecutionService.executeCode(
                request.getCode(),
                request.getLanguage(),
                request.getUserId(),
                request.getInput()
        );

        if ("ERROR".equals(response.getStatus()) && response.getOutput() != null
                && response.getOutput().startsWith("Error: Language")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<CodeResponse>> getHistory(@PathVariable String userId) {
        logger.info("Received history request for userId: {}", userId);

        List<Submission> submissions = codeExecutionService.getHistoryByUserId(userId);

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
    public ResponseEntity<String> getCodeById(@PathVariable Long id) {
        logger.info("Received code retrieval request for id: {}", id);

        try {
            String code = codeExecutionService.getCodeById(id);
            return ResponseEntity.ok(code);
        } catch (Exception e) {
            logger.error("Error retrieving code: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
