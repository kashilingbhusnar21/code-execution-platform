package com.codeplatform.code_executor.controller;

import com.codeplatform.code_executor.dto.CodeResponse;
import com.codeplatform.code_executor.dto.CodeSubmissionMessage;
import com.codeplatform.code_executor.dto.RunResponse;
import com.codeplatform.code_executor.dto.TestCaseItem;
import com.codeplatform.code_executor.dto.TestCaseResultItem;
import com.codeplatform.code_executor.entity.Problem;
import com.codeplatform.code_executor.entity.Submission;
import com.codeplatform.code_executor.entity.SubmissionStatus;
import com.codeplatform.code_executor.entity.TestCase;
import com.codeplatform.code_executor.model.CodeRequest;
import com.codeplatform.code_executor.repository.ProblemRepository;
import com.codeplatform.code_executor.repository.SubmissionRepository;
import com.codeplatform.code_executor.repository.TestCaseRepository;
import com.codeplatform.code_executor.service.CodeExecutionService;
import com.codeplatform.code_executor.service.RabbitMQProducerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/code")
public class CodeExecutionController {

    private static final Logger logger = LoggerFactory.getLogger(CodeExecutionController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CodeExecutionService codeExecutionService;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @PostMapping("/run")
    public ResponseEntity<RunResponse> executeCode(@RequestBody CodeRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");

        logger.info("Received code execution request (Run Code): language={}, userId={}",
                request.getLanguage(), userId);

        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: code is required"));
        }

        if (request.getLanguage() == null || request.getLanguage().trim().isEmpty()) {
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
        if (request.getProblemId() != null) {
            submission.setProblemId(request.getProblemId());
        }
        submissionRepository.save(submission);
        logger.info("Submission created for Run Code with PENDING status: id={}", submission.getId());

        // Resolve problemTitle if problemId is provided
        String problemTitle = null;
        if (request.getProblemId() != null) {
            problemTitle = problemRepository.findById(request.getProblemId())
                    .map(Problem::getTitle)
                    .orElse(null);
        } else if (request.getProblemTitle() != null && !request.getProblemTitle().isEmpty()) {
            problemTitle = request.getProblemTitle();
        }

        // Send to RabbitMQ for async processing (mode = RUN)
        CodeSubmissionMessage message = new CodeSubmissionMessage(
                submission.getId(),
                request.getLanguage(),
                request.getCode(),
                request.getInput(),
                userId,
                request.getProblemId(),
                problemTitle,
                "RUN",
                null
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

    @PostMapping("/submit")
    public ResponseEntity<RunResponse> submitCode(@RequestBody CodeRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");

        logger.info("Received code submission request (Submit Evaluation): language={}, problemId={}, userId={}",
                request.getLanguage(), request.getProblemId(), userId);

        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: code is required"));
        }

        if (request.getLanguage() == null || request.getLanguage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: language is required"));
        }

        if (userId == null) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: userId is required"));
        }

        if (request.getProblemId() == null) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: problemId is required for submission"));
        }

        Problem problem = problemRepository.findById(request.getProblemId()).orElse(null);
        if (problem == null) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: Problem not found with id " + request.getProblemId()));
        }

        List<TestCase> testCases = testCaseRepository.findByProblem(problem);
        if (testCases == null || testCases.isEmpty()) {
            return ResponseEntity.badRequest().body(RunResponse.error("Error: No test cases found for problem " + problem.getTitle()));
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
        submission.setProblemId(problem.getId());
        submission.setTotalCount(testCases.size());
        submission.setPassedCount(0);
        submissionRepository.save(submission);
        logger.info("Submission created for Submit with PENDING status: id={}, problemId={}, totalCases={}",
                submission.getId(), problem.getId(), testCases.size());

        // Prepare test cases for worker evaluation
        List<TestCaseItem> testCaseItems = testCases.stream()
                .map(tc -> new TestCaseItem(
                        tc.getId(),
                        tc.getInput(),
                        tc.getExpectedOutput(),
                        tc.getIsSample(),
                        tc.getIsHidden()
                ))
                .collect(Collectors.toList());

        // Send to RabbitMQ with mode = "SUBMIT"
        CodeSubmissionMessage message = new CodeSubmissionMessage(
                submission.getId(),
                request.getLanguage(),
                request.getCode(),
                null,
                userId,
                problem.getId(),
                problem.getTitle(),
                "SUBMIT",
                testCaseItems
        );
        rabbitMQProducerService.sendCodeSubmission(message);

        RunResponse response = new RunResponse();
        response.setOutput("Submission queued for evaluation. Checking results...");
        response.setStatus("PENDING");
        response.setExecutionTimeMs(0L);
        response.setSubmissionId(submission.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<CodeResponse>> getHistory(
            @RequestParam(required = false) Long problemId,
            HttpServletRequest httpRequest
    ) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        logger.info("Received history request - userId: {}, problemId: {}", userId, problemId);

        if (userId == null) {
            logger.error("userId is null - JWT filter may not be working");
            return ResponseEntity.badRequest().build();
        }

        List<Submission> submissions;
        if (problemId != null) {
            submissions = submissionRepository.findByUserIdAndProblemIdOrderByCreatedAtDesc(userId, problemId);
        } else {
            submissions = codeExecutionService.getHistoryByUserId(userId);
        }
        logger.info("Found {} submissions for userId: {}, problemId: {}", submissions.size(), userId, problemId);

        List<CodeResponse> responses = submissions.stream()
                .map(sub -> {
                    CodeResponse res = new CodeResponse(
                            sub.getId(),
                            sub.getLanguage(),
                            sub.getOutput(),
                            sub.getStatus().name(),
                            sub.getCreatedAt(),
                            sub.getExecutionTime()
                    );
                    res.setProblemId(sub.getProblemId());
                    res.setPassedCount(sub.getPassedCount());
                    res.setTotalCount(sub.getTotalCount());
                    res.setErrorDetails(sub.getErrorDetails());
                    return res;
                })
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

        List<TestCaseResultItem> testCaseResults = null;
        String outputStr = submission.getOutput();
        if (outputStr != null && outputStr.trim().startsWith("[")) {
            try {
                testCaseResults = objectMapper.readValue(outputStr, new TypeReference<List<TestCaseResultItem>>() {});
            } catch (Exception ignored) {
                // Not a JSON test case array, leave as plain output
            }
        }

        CodeResponse response = new CodeResponse(
                submission.getId(),
                submission.getLanguage(),
                outputStr,
                submission.getStatus().name(),
                submission.getCreatedAt(),
                submission.getExecutionTime(),
                submission.getProblemId(),
                submission.getPassedCount(),
                submission.getTotalCount(),
                submission.getErrorDetails(),
                testCaseResults
        );

        return ResponseEntity.ok(response);
    }
}
