package com.codeplatform.code_executor.dto;

import java.util.List;
import java.util.Map;

public class UserProgressDTO {
    private int totalProblems;
    private int solvedProblems;
    private int unsolvedProblems;
    private double progressPercentage;
    private List<Long> solvedProblemIds;
    private Map<String, DifficultyCountDTO> difficultyStats;
    private List<TopicCountDTO> topicStats;

    public UserProgressDTO() {
    }

    public UserProgressDTO(int totalProblems, int solvedProblems, int unsolvedProblems,
                           double progressPercentage, List<Long> solvedProblemIds,
                           Map<String, DifficultyCountDTO> difficultyStats,
                           List<TopicCountDTO> topicStats) {
        this.totalProblems = totalProblems;
        this.solvedProblems = solvedProblems;
        this.unsolvedProblems = unsolvedProblems;
        this.progressPercentage = progressPercentage;
        this.solvedProblemIds = solvedProblemIds;
        this.difficultyStats = difficultyStats;
        this.topicStats = topicStats;
    }

    public int getTotalProblems() {
        return totalProblems;
    }

    public void setTotalProblems(int totalProblems) {
        this.totalProblems = totalProblems;
    }

    public int getSolvedProblems() {
        return solvedProblems;
    }

    public void setSolvedProblems(int solvedProblems) {
        this.solvedProblems = solvedProblems;
    }

    public int getUnsolvedProblems() {
        return unsolvedProblems;
    }

    public void setUnsolvedProblems(int unsolvedProblems) {
        this.unsolvedProblems = unsolvedProblems;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public List<Long> getSolvedProblemIds() {
        return solvedProblemIds;
    }

    public void setSolvedProblemIds(List<Long> solvedProblemIds) {
        this.solvedProblemIds = solvedProblemIds;
    }

    public Map<String, DifficultyCountDTO> getDifficultyStats() {
        return difficultyStats;
    }

    public void setDifficultyStats(Map<String, DifficultyCountDTO> difficultyStats) {
        this.difficultyStats = difficultyStats;
    }

    public List<TopicCountDTO> getTopicStats() {
        return topicStats;
    }

    public void setTopicStats(List<TopicCountDTO> topicStats) {
        this.topicStats = topicStats;
    }

    public static class DifficultyCountDTO {
        private long solved;
        private long total;

        public DifficultyCountDTO() {
        }

        public DifficultyCountDTO(long solved, long total) {
            this.solved = solved;
            this.total = total;
        }

        public long getSolved() {
            return solved;
        }

        public void setSolved(long solved) {
            this.solved = solved;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }

    public static class TopicCountDTO {
        private String name;
        private long solved;
        private long total;

        public TopicCountDTO() {
        }

        public TopicCountDTO(String name, long solved, long total) {
            this.name = name;
            this.solved = solved;
            this.total = total;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getSolved() {
            return solved;
        }

        public void setSolved(long solved) {
            this.solved = solved;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }
}
