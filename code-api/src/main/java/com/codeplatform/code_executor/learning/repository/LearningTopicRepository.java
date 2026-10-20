package com.codeplatform.code_executor.learning.repository;

import com.codeplatform.code_executor.learning.entity.LearningTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningTopicRepository extends JpaRepository<LearningTopic, Long> {

    Optional<LearningTopic> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<LearningTopic> findByPhaseIdOrderBySequenceOrderAsc(Long phaseId);

    @Query("SELECT t FROM LearningTopic t WHERE t.phase.careerPath.slug = :trackSlug ORDER BY t.phase.phaseNumber ASC, t.sequenceOrder ASC")
    List<LearningTopic> findByCareerPathSlugOrderByOrder(@Param("trackSlug") String trackSlug);

    long countByPhaseCareerPathId(Long careerPathId);

    long countByPhaseCareerPathSlug(String trackSlug);
}
