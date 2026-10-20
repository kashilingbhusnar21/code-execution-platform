package com.codeplatform.code_executor.learning.repository;

import com.codeplatform.code_executor.learning.entity.CareerPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerPathRepository extends JpaRepository<CareerPath, Long> {

    Optional<CareerPath> findBySlug(String slug);

    List<CareerPath> findByIsActiveTrue();

    boolean existsBySlug(String slug);

    @Query("SELECT cp FROM CareerPath cp LEFT JOIN FETCH cp.phases p LEFT JOIN FETCH p.topics t WHERE cp.slug = :slug")
    Optional<CareerPath> findBySlugWithPhasesAndTopics(@Param("slug") String slug);
}
