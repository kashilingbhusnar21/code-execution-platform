package com.codeplatform.code_executor.learning.controller;

import com.codeplatform.code_executor.learning.dto.*;
import com.codeplatform.code_executor.learning.service.LearningService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning")
@CrossOrigin(origins = "*")
public class LearningController {

    private static final Logger logger = LoggerFactory.getLogger(LearningController.class);

    @Autowired
    private LearningService learningService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get all active career tracks (public, progress included if logged in).
     */
    @GetMapping("/tracks")
    public ResponseEntity<List<CareerPathDTO>> getAllTracks(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        logger.info("GET /api/learning/tracks - userId: {}", userId);
        List<CareerPathDTO> tracks = learningService.getAllTracks(userId);
        return ResponseEntity.ok(tracks);
    }

    /**
     * Get full roadmap for a track (public, user progress included if logged in).
     */
    @GetMapping("/tracks/{trackSlug}")
    public ResponseEntity<?> getTrackRoadmap(@PathVariable String trackSlug, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        logger.info("GET /api/learning/tracks/{} - userId: {}", trackSlug, userId);
        try {
            CareerPathDTO track = learningService.getTrackRoadmap(trackSlug, userId);
            return ResponseEntity.ok(track);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Get canonical manifest for a track from classpath.
     */
    @GetMapping("/manifest/{trackSlug}")
    public ResponseEntity<?> getManifest(@PathVariable String trackSlug) {
        if (!"java-backend".equalsIgnoreCase(trackSlug) && !"java-fullstack".equalsIgnoreCase(trackSlug)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Track manifest not found: " + trackSlug));
        }

        String manifestFile = "java-fullstack".equalsIgnoreCase(trackSlug)
                ? "/learning/content-manifest-fullstack.json"
                : "/learning/content-manifest-backend.json";

        try (InputStream is = getClass().getResourceAsStream(manifestFile)) {
            if (is == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Manifest not found for " + trackSlug));
            }
            JsonNode root = objectMapper.readTree(is);
            return ResponseEntity.ok(root);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Get topic metadata (public, user status included if logged in).
     */
    @GetMapping("/topics/{topicSlug}")
    public ResponseEntity<?> getTopicBySlug(@PathVariable String topicSlug, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        logger.info("GET /api/learning/topics/{} - userId: {}", topicSlug, userId);
        try {
            LearningTopicDTO topic = learningService.getTopicBySlug(topicSlug, userId);
            return ResponseEntity.ok(topic);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Get current user's progress for a track (requires authentication).
     */
    @GetMapping("/progress/{trackSlug}")
    public ResponseEntity<?> getUserTrackProgress(@PathVariable String trackSlug, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            logger.warn("GET /api/learning/progress/{} - Unauthorized", trackSlug);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }
        try {
            UserTrackProgressDTO progress = learningService.getUserTrackProgress(userId, trackSlug);
            return ResponseEntity.ok(progress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Update current user's topic status or quiz score (requires authentication).
     */
    @PostMapping("/topics/{topicSlug}/progress")
    public ResponseEntity<?> updateTopicProgress(
            @PathVariable String topicSlug,
            @RequestBody(required = false) TopicStatusUpdateRequest updateRequest,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            logger.warn("POST /api/learning/topics/{}/progress - Unauthorized", topicSlug);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }

        if (updateRequest == null) {
            updateRequest = new TopicStatusUpdateRequest("COMPLETED", null);
        }

        try {
            UserTopicProgressDTO result = learningService.updateTopicProgress(userId, topicSlug, updateRequest);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
