package com.codeplatform.code_executor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "test_cases")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "input", columnDefinition = "TEXT", nullable = false)
    private String input;

    @Column(name = "expected_output", columnDefinition = "TEXT", nullable = false)
    private String expectedOutput;

    @Column(name = "is_sample", nullable = false)
    private Boolean isSample = false;

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden = false;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    public TestCase() {
    }

    public TestCase(Problem problem, String input, String expectedOutput, Boolean isSample, Boolean isHidden) {
        this.problem = problem;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.isSample = isSample;
        this.isHidden = isHidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestCase)) return false;
        TestCase testCase = (TestCase) o;
        return id != null && id.equals(testCase.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
