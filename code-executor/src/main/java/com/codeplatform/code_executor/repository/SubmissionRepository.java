package com.codeplatform.code_executor.repository;

import com.codeplatform.code_executor.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Submission> findByIdAndUserId(Long id, Long userId);
}
