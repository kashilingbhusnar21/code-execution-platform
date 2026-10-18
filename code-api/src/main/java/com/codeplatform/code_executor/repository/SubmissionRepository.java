package com.codeplatform.code_executor.repository;

import com.codeplatform.code_executor.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Submission> findByUserIdAndProblemIdOrderByCreatedAtDesc(Long userId, Long problemId);

    Optional<Submission> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT DISTINCT s.problemId FROM Submission s WHERE s.userId = :userId AND s.status = com.codeplatform.code_executor.entity.SubmissionStatus.ACCEPTED AND s.problemId IS NOT NULL")
    List<Long> findSolvedProblemIdsByUserId(@Param("userId") Long userId);
}
