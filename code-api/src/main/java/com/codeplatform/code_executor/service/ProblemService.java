package com.codeplatform.code_executor.service;

import com.codeplatform.code_executor.dto.ProblemCreateRequest;
import com.codeplatform.code_executor.dto.ProblemDTO;
import com.codeplatform.code_executor.dto.TestCaseCreateRequest;
import com.codeplatform.code_executor.dto.TestCaseDTO;
import com.codeplatform.code_executor.dto.TopicDTO;
import com.codeplatform.code_executor.dto.UserProgressDTO;
import com.codeplatform.code_executor.entity.Problem;
import com.codeplatform.code_executor.entity.Problem.Difficulty;
import com.codeplatform.code_executor.entity.TestCase;
import com.codeplatform.code_executor.entity.Topic;
import com.codeplatform.code_executor.repository.ProblemRepository;
import com.codeplatform.code_executor.repository.SubmissionRepository;
import com.codeplatform.code_executor.repository.TestCaseRepository;
import com.codeplatform.code_executor.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProblemService {

    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    public UserProgressDTO getUserProgress(Long userId) {
        logger.info("Calculating user progress for userId: {}", userId);

        List<Long> solvedIds = submissionRepository.findSolvedProblemIdsByUserId(userId);
        Set<Long> solvedSet = new HashSet<>(solvedIds != null ? solvedIds : Collections.emptyList());

        List<Problem> allProblems = problemRepository.findAll();
        int totalProblems = allProblems.size();
        int uniqueSolvedCount = (int) allProblems.stream().filter(p -> solvedSet.contains(p.getId())).count();
        int unsolvedCount = Math.max(0, totalProblems - uniqueSolvedCount);
        double progressPercentage = totalProblems > 0 ? Math.round(((double) uniqueSolvedCount / totalProblems) * 1000.0) / 10.0 : 0.0;

        // Solved counts by difficulty
        Map<String, UserProgressDTO.DifficultyCountDTO> difficultyStats = new LinkedHashMap<>();
        for (Difficulty diff : Difficulty.values()) {
            long totalForDiff = allProblems.stream().filter(p -> p.getDifficulty() == diff).count();
            long solvedForDiff = allProblems.stream().filter(p -> p.getDifficulty() == diff && solvedSet.contains(p.getId())).count();
            difficultyStats.put(diff.name(), new UserProgressDTO.DifficultyCountDTO(solvedForDiff, totalForDiff));
        }

        // Solved counts by topic
        List<Topic> allTopics = topicRepository.findAll();
        List<UserProgressDTO.TopicCountDTO> topicStats = new ArrayList<>();
        for (Topic topic : allTopics) {
            long totalForTopic = allProblems.stream()
                    .filter(p -> p.getTopics() != null && p.getTopics().stream().anyMatch(t -> t.getName().equalsIgnoreCase(topic.getName())))
                    .count();
            if (totalForTopic > 0) {
                long solvedForTopic = allProblems.stream()
                        .filter(p -> solvedSet.contains(p.getId()) && p.getTopics() != null && p.getTopics().stream().anyMatch(t -> t.getName().equalsIgnoreCase(topic.getName())))
                        .count();
                topicStats.add(new UserProgressDTO.TopicCountDTO(topic.getName(), solvedForTopic, totalForTopic));
            }
        }

        return new UserProgressDTO(
                totalProblems,
                uniqueSolvedCount,
                unsolvedCount,
                progressPercentage,
                new ArrayList<>(solvedSet),
                difficultyStats,
                topicStats
        );
    }

    public List<ProblemDTO> getAllProblems() {
        logger.info("Fetching all problems");
        return problemRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProblemDTO getProblemById(Long id) {
        logger.info("Fetching problem with id: {}", id);
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        return convertToDTO(problem);
    }

    public List<ProblemDTO> getProblemsByDifficulty(Difficulty difficulty) {
        logger.info("Fetching problems with difficulty: {}", difficulty);
        return problemRepository.findByDifficulty(difficulty).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProblemDTO> getProblemsByTopic(String topicName) {
        logger.info("Fetching problems with topic: {}", topicName);
        return problemRepository.findByTopicName(topicName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProblemDTO> searchProblems(String keyword) {
        logger.info("Searching problems with keyword: {}", keyword);
        return problemRepository.searchByKeyword(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProblemDTO> filterProblems(Difficulty difficulty, Long topicId) {
        logger.info("Filtering problems by difficulty: {} and topicId: {}", difficulty, topicId);
        return problemRepository.findByDifficultyAndTopic(difficulty, topicId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProblemDTO createProblem(ProblemCreateRequest request) {
        logger.info("Creating new problem: {}", request.getTitle());

        Problem problem = new Problem();
        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());
        problem.setTimeLimit(request.getTimeLimit());
        problem.setMemoryLimit(request.getMemoryLimit());
        problem.setConstraints(request.getConstraints());
        problem.setStarterCode(request.getStarterCode());

        // Handle topics
        if (request.getTopics() != null && !request.getTopics().isEmpty()) {
            Set<Topic> topics = request.getTopics().stream()
                    .map(topicName -> {
                        Topic topic = topicRepository.findByName(topicName)
                                .orElseGet(() -> {
                                    Topic newTopic = new Topic(topicName);
                                    return topicRepository.save(newTopic);
                                });
                        return topic;
                    })
                    .collect(Collectors.toSet());
            problem.setTopics(topics);
        }

        // Save problem first
        Problem savedProblem = problemRepository.save(problem);

        // Handle test cases
        if (request.getTestCases() != null && !request.getTestCases().isEmpty()) {
            Set<TestCase> testCases = request.getTestCases().stream()
                    .map(tcRequest -> {
                        TestCase testCase = new TestCase();
                        testCase.setProblem(savedProblem);
                        testCase.setInput(tcRequest.getInput());
                        testCase.setExpectedOutput(tcRequest.getExpectedOutput());
                        testCase.setIsSample(tcRequest.getIsSample() != null ? tcRequest.getIsSample() : false);
                        testCase.setIsHidden(tcRequest.getIsHidden() != null ? tcRequest.getIsHidden() : false);
                        testCase.setExplanation(tcRequest.getExplanation());
                        return testCase;
                    })
                    .collect(Collectors.toSet());
            savedProblem.setTestCases(testCases);
            testCaseRepository.saveAll(testCases);
        }

        return convertToDTO(savedProblem);
    }

    @Transactional
    public ProblemDTO updateProblem(Long id, ProblemCreateRequest request) {
        logger.info("Updating problem with id: {}", id);

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));

        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());
        problem.setTimeLimit(request.getTimeLimit());
        problem.setMemoryLimit(request.getMemoryLimit());
        problem.setConstraints(request.getConstraints());
        problem.setStarterCode(request.getStarterCode());

        // Update topics
        if (request.getTopics() != null) {
            Set<Topic> topics = request.getTopics().stream()
                    .map(topicName -> {
                        Topic topic = topicRepository.findByName(topicName)
                                .orElseGet(() -> {
                                    Topic newTopic = new Topic(topicName);
                                    return topicRepository.save(newTopic);
                                });
                        return topic;
                    })
                    .collect(Collectors.toSet());
            problem.setTopics(topics);
        }

        Problem updatedProblem = problemRepository.save(problem);
        return convertToDTO(updatedProblem);
    }

    @Transactional
    public void deleteProblem(Long id) {
        logger.info("Deleting problem with id: {}", id);
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        problemRepository.delete(problem);
    }

    public List<TopicDTO> getAllTopics() {
        logger.info("Fetching all topics");
        return topicRepository.findAll().stream()
                .map(topic -> new TopicDTO(topic.getId(), topic.getName(), topic.getDescription()))
                .collect(Collectors.toList());
    }

    private ProblemDTO convertToDTO(Problem problem) {
        ProblemDTO dto = new ProblemDTO();
        dto.setId(problem.getId());
        dto.setTitle(problem.getTitle());
        dto.setDescription(problem.getDescription());
        dto.setDifficulty(problem.getDifficulty());
        dto.setTimeLimit(problem.getTimeLimit());
        dto.setMemoryLimit(problem.getMemoryLimit());
        dto.setConstraints(problem.getConstraints());
        dto.setStarterCode(problem.getStarterCode());
        dto.setCreatedAt(problem.getCreatedAt());
        dto.setUpdatedAt(problem.getUpdatedAt());

        // Convert topics
        Set<TopicDTO> topicDTOs = problem.getTopics().stream()
                .map(topic -> new TopicDTO(topic.getId(), topic.getName(), topic.getDescription()))
                .collect(Collectors.toSet());
        dto.setTopics(topicDTOs);

        // Convert only sample test cases (not hidden ones)
        Set<TestCaseDTO> sampleTestCaseDTOs = problem.getTestCases().stream()
                .filter(tc -> tc.getIsSample() && !tc.getIsHidden())
                .map(tc -> new TestCaseDTO(
                        tc.getId(),
                        tc.getInput(),
                        tc.getExpectedOutput(),
                        tc.getIsSample(),
                        tc.getIsHidden(),
                        tc.getExplanation()
                ))
                .collect(Collectors.toSet());
        dto.setSampleTestCases(sampleTestCaseDTOs);

        return dto;
    }
}
