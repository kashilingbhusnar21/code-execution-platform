package com.codeplatform.code_executor.repository;

import com.codeplatform.code_executor.entity.Problem;
import com.codeplatform.code_executor.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    List<TestCase> findByProblem(Problem problem);

    List<TestCase> findByProblemAndIsSample(Problem problem, Boolean isSample);

    List<TestCase> findByProblemAndIsHidden(Problem problem, Boolean isHidden);
}
