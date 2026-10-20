package com.codeplatform.code_executor.learning.service;

import com.codeplatform.code_executor.entity.User;
import com.codeplatform.code_executor.learning.dto.*;
import com.codeplatform.code_executor.learning.entity.*;
import com.codeplatform.code_executor.learning.repository.*;
import com.codeplatform.code_executor.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearningService {

    private static final Logger logger = LoggerFactory.getLogger(LearningService.class);

    @Autowired
    private CareerPathRepository careerPathRepository;

    @Autowired
    private RoadmapPhaseRepository roadmapPhaseRepository;

    @Autowired
    private LearningTopicRepository learningTopicRepository;

    @Autowired
    private UserTopicProgressRepository userTopicProgressRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all active career tracks. If userId is provided, attaches progress summaries.
     */
    @Transactional(readOnly = true)
    public List<CareerPathDTO> getAllTracks(Long userId) {
        List<CareerPath> paths = careerPathRepository.findByIsActiveTrue();
        List<CareerPathDTO> result = new ArrayList<>();

        for (CareerPath path : paths) {
            long totalTopics = learningTopicRepository.countByPhaseCareerPathId(path.getId());
            UserTrackProgressDTO progressDTO = null;

            if (userId != null) {
                progressDTO = getUserTrackProgress(userId, path.getSlug());
            }

            CareerPathDTO dto = CareerPathDTO.builder()
                    .id(path.getId())
                    .slug(path.getSlug())
                    .title(path.getTitle())
                    .description(path.getDescription())
                    .icon(path.getIcon())
                    .totalPhases(path.getTotalPhases())
                    .totalTopics((int) totalTopics)
                    .estimatedWeeks(path.getEstimatedWeeks())
                    .isActive(path.getIsActive())
                    .userProgress(progressDTO)
                    .build();

            result.add(dto);
        }

        return result;
    }

    /**
     * Get complete roadmap for a career path with phases and topics.
     * Enriches with user topic progress if userId is provided.
     */
    @Transactional(readOnly = true)
    public CareerPathDTO getTrackRoadmap(String trackSlug, Long userId) {
        CareerPath careerPath = careerPathRepository.findBySlug(trackSlug)
                .orElseThrow(() -> new IllegalArgumentException("Career track not found: " + trackSlug));

        List<RoadmapPhase> phases = roadmapPhaseRepository.findByCareerPathSlugOrderByPhaseNumberAsc(trackSlug);

        // Fetch user progress for this track if authenticated
        Map<Long, UserTopicProgress> userProgressByTopicId = new HashMap<>();
        if (userId != null) {
            List<UserTopicProgress> userProgressList = userTopicProgressRepository.findByUserIdAndTrackSlug(userId, trackSlug);
            for (UserTopicProgress utp : userProgressList) {
                userProgressByTopicId.put(utp.getTopic().getId(), utp);
            }
        }

        int totalTrackTopics = 0;
        int completedTrackTopics = 0;
        List<RoadmapPhaseDTO> phaseDTOs = new ArrayList<>();

        for (RoadmapPhase phase : phases) {
            List<LearningTopic> topics = learningTopicRepository.findByPhaseIdOrderBySequenceOrderAsc(phase.getId());
            List<LearningTopicDTO> topicDTOs = new ArrayList<>();
            int completedPhaseTopics = 0;

            for (LearningTopic topic : topics) {
                totalTrackTopics++;
                UserTopicProgress utp = userProgressByTopicId.get(topic.getId());

                String status = (utp != null) ? utp.getStatus().name() : TopicProgressStatus.NOT_STARTED.name();
                Integer quizScore = (utp != null) ? utp.getQuizScore() : null;
                LocalDateTime completedAt = (utp != null) ? utp.getCompletedAt() : null;

                if (TopicProgressStatus.COMPLETED.name().equals(status)) {
                    completedPhaseTopics++;
                    completedTrackTopics++;
                }

                LearningTopicDTO topicDTO = LearningTopicDTO.builder()
                        .id(topic.getId())
                        .sequenceOrder(topic.getSequenceOrder())
                        .slug(topic.getSlug())
                        .title(topic.getTitle())
                        .summary(topic.getSummary())
                        .contentPath(topic.getContentPath())
                        .estimatedMinutes(topic.getEstimatedMinutes())
                        .totalLearningHours(topic.getTotalLearningHours())
                        .tier(topic.getTier())
                        .hasQuiz(topic.getHasQuiz())
                        .hasProject(topic.getHasProject())
                        .status(status)
                        .quizScore(quizScore)
                        .completedAt(completedAt)
                        .build();

                topicDTOs.add(topicDTO);
            }

            RoadmapPhaseDTO phaseDTO = RoadmapPhaseDTO.builder()
                    .id(phase.getId())
                    .phaseNumber(phase.getPhaseNumber())
                    .title(phase.getTitle())
                    .description(phase.getDescription())
                    .estimatedHours(phase.getEstimatedHours())
                    .color(phase.getColor())
                    .topics(topicDTOs)
                    .totalTopics(topics.size())
                    .completedTopics(completedPhaseTopics)
                    .build();

            phaseDTOs.add(phaseDTO);
        }

        UserTrackProgressDTO trackProgressDTO = null;
        if (userId != null) {
            double completionRate = totalTrackTopics > 0 ? ((double) completedTrackTopics / totalTrackTopics) * 100.0 : 0.0;
            Map<String, String> statusMap = new HashMap<>();
            for (UserTopicProgress utp : userProgressByTopicId.values()) {
                statusMap.put(utp.getTopic().getSlug(), utp.getStatus().name());
            }

            trackProgressDTO = UserTrackProgressDTO.builder()
                    .trackSlug(trackSlug)
                    .trackTitle(careerPath.getTitle())
                    .totalTopics(totalTrackTopics)
                    .completedTopics(completedTrackTopics)
                    .inProgressTopics((int) userProgressByTopicId.values().stream()
                            .filter(p -> p.getStatus() == TopicProgressStatus.IN_PROGRESS).count())
                    .completionPercentage(Math.round(completionRate * 10.0) / 10.0)
                    .topicStatuses(statusMap)
                    .build();
        }

        return CareerPathDTO.builder()
                .id(careerPath.getId())
                .slug(careerPath.getSlug())
                .title(careerPath.getTitle())
                .description(careerPath.getDescription())
                .icon(careerPath.getIcon())
                .totalPhases(careerPath.getTotalPhases())
                .totalTopics(totalTrackTopics)
                .estimatedWeeks(careerPath.getEstimatedWeeks())
                .isActive(careerPath.getIsActive())
                .phases(phaseDTOs)
                .userProgress(trackProgressDTO)
                .build();
    }

    /**
     * Get a single topic by slug with progress if userId provided.
     */
    @Transactional(readOnly = true)
    public LearningTopicDTO getTopicBySlug(String topicSlug, Long userId) {
        LearningTopic topic = learningTopicRepository.findBySlug(topicSlug)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + topicSlug));

        String status = TopicProgressStatus.NOT_STARTED.name();
        Integer quizScore = null;
        LocalDateTime completedAt = null;

        if (userId != null) {
            Optional<UserTopicProgress> utpOpt = userTopicProgressRepository.findByUserIdAndTopicSlug(userId, topicSlug);
            if (utpOpt.isPresent()) {
                UserTopicProgress utp = utpOpt.get();
                status = utp.getStatus().name();
                quizScore = utp.getQuizScore();
                completedAt = utp.getCompletedAt();
            }
        }

        return LearningTopicDTO.builder()
                .id(topic.getId())
                .sequenceOrder(topic.getSequenceOrder())
                .slug(topic.getSlug())
                .title(topic.getTitle())
                .summary(topic.getSummary())
                .contentPath(topic.getContentPath())
                .estimatedMinutes(topic.getEstimatedMinutes())
                .totalLearningHours(topic.getTotalLearningHours())
                .tier(topic.getTier())
                .hasQuiz(topic.getHasQuiz())
                .hasProject(topic.getHasProject())
                .status(status)
                .quizScore(quizScore)
                .completedAt(completedAt)
                .build();
    }

    /**
     * Get user's progress for a specific career track.
     */
    @Transactional(readOnly = true)
    public UserTrackProgressDTO getUserTrackProgress(Long userId, String trackSlug) {
        CareerPath careerPath = careerPathRepository.findBySlug(trackSlug)
                .orElseThrow(() -> new IllegalArgumentException("Career track not found: " + trackSlug));

        List<LearningTopic> topics = learningTopicRepository.findByCareerPathSlugOrderByOrder(trackSlug);
        List<UserTopicProgress> userProgress = userTopicProgressRepository.findByUserIdAndTrackSlug(userId, trackSlug);

        Map<Long, UserTopicProgress> progressMap = userProgress.stream()
                .collect(Collectors.toMap(p -> p.getTopic().getId(), p -> p));

        int completed = 0;
        int inProgress = 0;
        String lastActiveTopicSlug = null;
        Map<String, String> statusMap = new HashMap<>();

        for (LearningTopic t : topics) {
            UserTopicProgress p = progressMap.get(t.getId());
            if (p != null) {
                statusMap.put(t.getSlug(), p.getStatus().name());
                if (p.getStatus() == TopicProgressStatus.COMPLETED) {
                    completed++;
                } else if (p.getStatus() == TopicProgressStatus.IN_PROGRESS) {
                    inProgress++;
                    lastActiveTopicSlug = t.getSlug();
                }
            } else {
                statusMap.put(t.getSlug(), TopicProgressStatus.NOT_STARTED.name());
            }
        }

        double percentage = !topics.isEmpty() ? ((double) completed / topics.size()) * 100.0 : 0.0;

        return UserTrackProgressDTO.builder()
                .trackSlug(trackSlug)
                .trackTitle(careerPath.getTitle())
                .totalTopics(topics.size())
                .completedTopics(completed)
                .inProgressTopics(inProgress)
                .completionPercentage(Math.round(percentage * 10.0) / 10.0)
                .lastActiveTopicSlug(lastActiveTopicSlug)
                .topicStatuses(statusMap)
                .build();
    }

    /**
     * Update user's progress on a topic (start reading, complete topic, record quiz score).
     */
    @Transactional
    public UserTopicProgressDTO updateTopicProgress(Long userId, String topicSlug, TopicStatusUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        LearningTopic topic = learningTopicRepository.findBySlug(topicSlug)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + topicSlug));

        UserTopicProgress progress = userTopicProgressRepository.findByUserIdAndTopicId(userId, topic.getId())
                .orElseGet(() -> {
                    UserTopicProgress newProgress = new UserTopicProgress();
                    newProgress.setUser(user);
                    newProgress.setTopic(topic);
                    newProgress.setStatus(TopicProgressStatus.NOT_STARTED);
                    return newProgress;
                });

        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            try {
                TopicProgressStatus newStatus = TopicProgressStatus.valueOf(request.getStatus().toUpperCase().trim());
                progress.setStatus(newStatus);
                if (newStatus == TopicProgressStatus.COMPLETED && progress.getCompletedAt() == null) {
                    progress.setCompletedAt(LocalDateTime.now());
                }
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status requested: {}", request.getStatus());
            }
        }

        if (request.getQuizScore() != null) {
            progress.setQuizScore(request.getQuizScore());
        }

        progress = userTopicProgressRepository.save(progress);
        logger.info("Updated progress for user {} on topic {}: status={}, score={}",
                userId, topicSlug, progress.getStatus(), progress.getQuizScore());

        return UserTopicProgressDTO.builder()
                .topicSlug(topic.getSlug())
                .topicTitle(topic.getTitle())
                .status(progress.getStatus().name())
                .quizScore(progress.getQuizScore())
                .completedAt(progress.getCompletedAt())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }
}
