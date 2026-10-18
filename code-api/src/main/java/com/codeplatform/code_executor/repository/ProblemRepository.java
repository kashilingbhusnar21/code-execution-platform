package com.codeplatform.code_executor.repository;

import com.codeplatform.code_executor.entity.Problem;
import com.codeplatform.code_executor.entity.Problem.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    List<Problem> findByDifficulty(Difficulty difficulty);

    @Query("SELECT p FROM Problem p JOIN p.topics t WHERE t.name = :topicName")
    List<Problem> findByTopicName(@Param("topicName") String topicName);

    @Query("SELECT p FROM Problem p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Problem> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Problem p JOIN p.topics t WHERE t.id = :topicId")
    List<Problem> findByTopicId(@Param("topicId") Long topicId);

    @Query("SELECT p FROM Problem p WHERE p.difficulty = :difficulty AND (:topicId IS NULL OR :topicId IN (SELECT t.id FROM p.topics t))")
    List<Problem> findByDifficultyAndTopic(@Param("difficulty") Difficulty difficulty, @Param("topicId") Long topicId);

    Problem findByTitle(String title);
}
