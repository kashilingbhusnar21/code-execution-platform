package com.codeplatform.code_executor.controller;

import com.codeplatform.code_executor.dto.ProblemCreateRequest;
import com.codeplatform.code_executor.dto.ProblemDTO;
import com.codeplatform.code_executor.dto.TopicDTO;
import com.codeplatform.code_executor.dto.UserProgressDTO;
import com.codeplatform.code_executor.entity.Problem.Difficulty;
import com.codeplatform.code_executor.service.ProblemService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/problems")
@CrossOrigin(origins = "*")
public class ProblemController {

    private static final Logger logger = LoggerFactory.getLogger(ProblemController.class);

    @Autowired
    private ProblemService problemService;

    @GetMapping("/progress")
    public ResponseEntity<?> getUserProgress(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            logger.warn("GET /api/problems/progress - Unauthorized request (missing or invalid token)");
            return ResponseEntity.status(401).body(Map.of("message", "Authentication required"));
        }
        logger.info("GET /api/problems/progress - Fetching progress for userId: {}", userId);
        UserProgressDTO progress = problemService.getUserProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping
    public ResponseEntity<List<ProblemDTO>> getAllProblems() {
        logger.info("GET /api/problems - Fetching all problems");
        List<ProblemDTO> problems = problemService.getAllProblems();
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemDTO> getProblemById(@PathVariable Long id) {
        logger.info("GET /api/problems/{} - Fetching problem by id", id);
        ProblemDTO problem = problemService.getProblemById(id);
        return ResponseEntity.ok(problem);
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<ProblemDTO>> getProblemsByDifficulty(@PathVariable Difficulty difficulty) {
        logger.info("GET /api/problems/difficulty/{} - Fetching problems by difficulty", difficulty);
        List<ProblemDTO> problems = problemService.getProblemsByDifficulty(difficulty);
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/topic/{topicName}")
    public ResponseEntity<List<ProblemDTO>> getProblemsByTopic(@PathVariable String topicName) {
        logger.info("GET /api/problems/topic/{} - Fetching problems by topic", topicName);
        List<ProblemDTO> problems = problemService.getProblemsByTopic(topicName);
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProblemDTO>> searchProblems(@RequestParam String keyword) {
        logger.info("GET /api/problems/search?keyword={} - Searching problems", keyword);
        List<ProblemDTO> problems = problemService.searchProblems(keyword);
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProblemDTO>> filterProblems(
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) Long topicId) {
        logger.info("GET /api/problems/filter - Filtering problems by difficulty: {}, topicId: {}", difficulty, topicId);
        List<ProblemDTO> problems = problemService.filterProblems(difficulty, topicId);
        return ResponseEntity.ok(problems);
    }

    @PostMapping
    public ResponseEntity<ProblemDTO> createProblem(@RequestBody ProblemCreateRequest request) {
        logger.info("POST /api/problems - Creating new problem: {}", request.getTitle());
        ProblemDTO problem = problemService.createProblem(request);
        return ResponseEntity.ok(problem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemDTO> updateProblem(
            @PathVariable Long id,
            @RequestBody ProblemCreateRequest request) {
        logger.info("PUT /api/problems/{} - Updating problem", id);
        ProblemDTO problem = problemService.updateProblem(id, request);
        return ResponseEntity.ok(problem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        logger.info("DELETE /api/problems/{} - Deleting problem", id);
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/topics/all")
    public ResponseEntity<List<TopicDTO>> getAllTopics() {
        logger.info("GET /api/problems/topics/all - Fetching all topics");
        List<TopicDTO> topics = problemService.getAllTopics();
        return ResponseEntity.ok(topics);
    }
}
