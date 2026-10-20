package com.codeplatform.code_executor.learning.repository;

import com.codeplatform.code_executor.learning.entity.RoadmapPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapPhaseRepository extends JpaRepository<RoadmapPhase, Long> {

    List<RoadmapPhase> findByCareerPathIdOrderByPhaseNumberAsc(Long careerPathId);

    List<RoadmapPhase> findByCareerPathSlugOrderByPhaseNumberAsc(String slug);
}
