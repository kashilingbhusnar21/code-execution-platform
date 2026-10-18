package com.codeplatform.code_executor.executor;

import com.codeplatform.code_executor.entity.SubmissionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class CppExecutor extends AbstractDockerExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CppExecutor.class);

    @Override
    public String getLanguage() {
        return "cpp";
    }

    @Override
    public String getSourceFileName() {
        return "main.cpp";
    }

    @Override
    public ExecutionResult execute(String code, String filePath, String input) {
        return execute(code, filePath, input, null);
    }

    @Override
    public ExecutionResult execute(String code, String filePath, String input, String problemTitle) {
        long startTime = System.currentTimeMillis();
        StringBuilder output = new StringBuilder();
        SubmissionStatus status = SubmissionStatus.SUCCESS;

        Path sourceFile = Paths.get(filePath).toAbsolutePath().normalize();
        Path workDir = sourceFile.getParent();

        try {
            boolean isProblem = DsaProblemType.isProblemMode(problemTitle, code);
            DsaProblemDef problemDef = isProblem ? DsaProblemType.from(problemTitle, code) : null;

            if (isProblem && problemDef == null) {
                status = SubmissionStatus.ERROR;
                output.append("Error: Unsupported problem type: '").append(problemTitle != null ? problemTitle : "unknown")
                      .append("'. Automated harness is not configured for this problem.");
                return new ExecutionResult(output.toString().trim(), status, System.currentTimeMillis() - startTime);
            }

            if (problemDef != null) {
                logger.info("C++ DSA problem mode detected: {}", problemDef);
                // 1. Write user solution to solution.h with common definitions
                Path solutionHeader = workDir.resolve("solution.h");
                String solutionContent = "#pragma once\n"
                        + "#include <iostream>\n"
                        + "#include <vector>\n"
                        + "#include <string>\n"
                        + "#include <algorithm>\n"
                        + "#include <stack>\n"
                        + "#include <unordered_map>\n"
                        + "#include <map>\n"
                        + "#include <set>\n"
                        + "#include <climits>\n"
                        + "using namespace std;\n\n"
                        + "struct ListNode {\n"
                        + "    int val;\n"
                        + "    ListNode *next;\n"
                        + "    ListNode() : val(0), next(nullptr) {}\n"
                        + "    ListNode(int x) : val(x), next(nullptr) {}\n"
                        + "    ListNode(int x, ListNode *next) : val(x), next(next) {}\n"
                        + "};\n\n"
                        + "struct TreeNode {\n"
                        + "    int val;\n"
                        + "    TreeNode *left;\n"
                        + "    TreeNode *right;\n"
                        + "    TreeNode() : val(0), left(nullptr), right(nullptr) {}\n"
                        + "    TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}\n"
                        + "    TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}\n"
                        + "};\n\n"
                        + code;
                java.nio.file.Files.writeString(solutionHeader, solutionContent);

                // 2. Write harness to main.cpp
                Path mainFile = workDir.resolve("main.cpp");
                String harnessCode = DsaHarnessGenerator.getCppHarness(problemDef);
                java.nio.file.Files.writeString(mainFile, harnessCode);
            }

            logger.info("C++ work dir: {}", workDir);

            // docker exec cpp-container g++ main.cpp -o main
            CommandResult compileResult = runInDocker(workDir, "g++ main.cpp -o main", null);

            if (compileResult.isTimedOut()) {
                status = SubmissionStatus.TIMEOUT;
                output.append(compileResult.getOutput());
            } else if (!compileResult.isSuccess()) {
                status = SubmissionStatus.COMPILATION_ERROR;
                output.append("Compilation Error:\n").append(compileResult.getOutput());
            } else {
                // docker exec cpp-container ./main
                CommandResult runResult = runInDocker(workDir, "./main", input);

                if (runResult.isTimedOut()) {
                    status = SubmissionStatus.TIMEOUT;
                } else if (!runResult.isSuccess()) {
                    status = SubmissionStatus.RUNTIME_ERROR;
                }
                output.append(runResult.getOutput());
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            logger.error("C++ execution failed", e);
            status = SubmissionStatus.RUNTIME_ERROR;
            output.append("Error executing code: ").append(e.getMessage());
        } finally {
            cleanupWorkDir(workDir);
        }

        return new ExecutionResult(
                output.toString().trim(),
                status,
                System.currentTimeMillis() - startTime
        );
    }
}
