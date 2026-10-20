package com.codeplatform.code_executor.learning.config;

import com.codeplatform.code_executor.learning.entity.CareerPath;
import com.codeplatform.code_executor.learning.entity.LearningTopic;
import com.codeplatform.code_executor.learning.entity.RoadmapPhase;
import com.codeplatform.code_executor.learning.repository.CareerPathRepository;
import com.codeplatform.code_executor.learning.repository.LearningTopicRepository;
import com.codeplatform.code_executor.learning.repository.RoadmapPhaseRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Optional;

@Component
@Order(20)
public class LearningDataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(LearningDataSeeder.class);

    @Autowired
    private CareerPathRepository careerPathRepository;

    @Autowired
    private RoadmapPhaseRepository roadmapPhaseRepository;

    @Autowired
    private LearningTopicRepository learningTopicRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedTrackFromManifest("/learning/content-manifest-backend.json");
        seedTrackFromManifest("/learning/content-manifest-fullstack.json");
    }

    private void seedTrackFromManifest(String manifestPath) {
        try (InputStream is = getClass().getResourceAsStream(manifestPath)) {
            if (is == null) {
                logger.warn("Content manifest not found on classpath: {}", manifestPath);
                return;
            }

            JsonNode root = objectMapper.readTree(is);
            String trackSlug = root.get("trackSlug").asText();
            String trackTitle = root.get("trackTitle").asText();
            String description = root.path("description").asText("");
            String icon = root.path("icon").asText("java");
            int totalPhases = root.path("totalPhases").asInt(12);
            int estimatedWeeks = root.path("estimatedWeeks").asInt(24);

            // 1. In-place idempotent upsert of CareerPath
            CareerPath careerPath = careerPathRepository.findBySlug(trackSlug)
                    .orElseGet(() -> {
                        CareerPath cp = new CareerPath();
                        cp.setSlug(trackSlug);
                        return cp;
                    });

            careerPath.setTitle(trackTitle);
            careerPath.setDescription(description);
            careerPath.setIcon(icon);
            careerPath.setTotalPhases(totalPhases);
            careerPath.setEstimatedWeeks(estimatedWeeks);
            careerPath.setIsActive(true);
            careerPath = careerPathRepository.save(careerPath);

            logger.info("Seeding/updating Career Path '{}' [{}]", trackTitle, trackSlug);

            // 2. Iterate through phases and topics
            JsonNode phasesNode = root.get("phases");
            if (phasesNode != null && phasesNode.isArray()) {
                for (JsonNode phaseNode : phasesNode) {
                    int phaseNumber = phaseNode.get("phaseNumber").asInt();
                    String phaseTitle = phaseNode.get("title").asText();
                    String phaseDesc = phaseNode.path("description").asText("");
                    int estimatedHours = phaseNode.path("estimatedHours").asInt(20);
                    String color = phaseNode.path("color").asText("from-sky-500 to-indigo-600");

                    RoadmapPhase phase = getOrCreatePhase(careerPath, phaseNumber, phaseTitle, phaseDesc, estimatedHours, color);

                    JsonNode topicsNode = phaseNode.get("topics");
                    if (topicsNode != null && topicsNode.isArray()) {
                        for (JsonNode topicNode : topicsNode) {
                            int seqOrder = topicNode.get("sequenceOrder").asInt();
                            String topicSlug = topicNode.get("slug").asText();
                            String topicTitle = topicNode.get("title").asText();
                            String summary = topicNode.path("summary").asText("");
                            String contentPath = topicNode.get("contentPath").asText();
                            int readingMinutes = topicNode.path("estimatedMinutes").asInt(45);
                            double totalLearningHours = topicNode.path("totalLearningHours").asDouble(2.5);
                            String tier = topicNode.path("tier").asText("CORE");
                            boolean hasQuiz = topicNode.path("hasQuiz").asBoolean(true);
                            boolean hasProject = topicNode.path("hasProject").asBoolean(false);

                            upsertTopic(phase, seqOrder, topicSlug, topicTitle, summary, contentPath, readingMinutes, totalLearningHours, tier, hasQuiz, hasProject);
                        }
                    }
                }
            }

            logger.info("Successfully synchronized Career Path '{}' with manifest.", trackTitle);
        } catch (Exception e) {
            logger.error("Failed to seed learning track from manifest {}: {}", manifestPath, e.getMessage(), e);
        }
    }

    private RoadmapPhase getOrCreatePhase(CareerPath careerPath, int phaseNumber, String title, String description, int estimatedHours, String color) {
        return roadmapPhaseRepository.findByCareerPathSlugOrderByPhaseNumberAsc(careerPath.getSlug()).stream()
                .filter(p -> p.getPhaseNumber() == phaseNumber)
                .findFirst()
                .map(existingPhase -> {
                    existingPhase.setTitle(title);
                    existingPhase.setDescription(description);
                    existingPhase.setEstimatedHours(estimatedHours);
                    existingPhase.setColor(color);
                    return roadmapPhaseRepository.save(existingPhase);
                })
                .orElseGet(() -> {
                    RoadmapPhase p = new RoadmapPhase();
                    p.setCareerPath(careerPath);
                    p.setPhaseNumber(phaseNumber);
                    p.setTitle(title);
                    p.setDescription(description);
                    p.setEstimatedHours(estimatedHours);
                    p.setColor(color);
                    return roadmapPhaseRepository.save(p);
                });
    }

    private void upsertTopic(RoadmapPhase phase, int sequenceOrder, String slug, String title, String summary,
                             String contentPath, int readingMinutes, double totalLearningHours, String tier,
                             boolean hasQuiz, boolean hasProject) {
        Optional<LearningTopic> existingOpt = learningTopicRepository.findBySlug(slug);

        LearningTopic topic = existingOpt.orElseGet(LearningTopic::new);
        topic.setPhase(phase);
        topic.setSequenceOrder(sequenceOrder);
        topic.setSlug(slug);
        topic.setTitle(title);
        topic.setSummary(summary);
        topic.setContentPath(contentPath);
        topic.setEstimatedMinutes(readingMinutes);
        topic.setTotalLearningHours(totalLearningHours);
        topic.setTier(tier);
        topic.setHasQuiz(hasQuiz);
        topic.setHasProject(hasProject);

        learningTopicRepository.save(topic);
    }
}
