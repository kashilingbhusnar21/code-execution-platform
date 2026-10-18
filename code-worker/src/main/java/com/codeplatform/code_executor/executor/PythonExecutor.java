package com.codeplatform.code_executor.executor;

import com.codeplatform.code_executor.entity.SubmissionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PythonExecutor extends AbstractDockerExecutor {

    private static final Logger logger = LoggerFactory.getLogger(PythonExecutor.class);

    @Override
    public String getLanguage() {
        return "python";
    }

    @Override
    public String getSourceFileName() {
        return "main.py";
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
                logger.info("Python DSA problem mode detected: {}", problemDef);
                // 1. Write user solution to solution.py with typing imports and definitions
                Path solutionFile = workDir.resolve("solution.py");
                String solutionContent = "from typing import List, Dict, Tuple, Set, Optional, Any\n\n"
                        + "class ListNode:\n"
                        + "    def __init__(self, val=0, next=None):\n"
                        + "        self.val = val\n"
                        + "        self.next = next\n\n"
                        + "class TreeNode:\n"
                        + "    def __init__(self, val=0, left=None, right=None):\n"
                        + "        self.val = val\n"
                        + "        self.left = left\n"
                        + "        self.right = right\n\n"
                        + code;
                java.nio.file.Files.writeString(solutionFile, solutionContent);

                // 2. Write harness to main.py
                Path mainFile = workDir.resolve("main.py");
                String harnessCode = DsaHarnessGenerator.getPythonHarness(problemDef);
                java.nio.file.Files.writeString(mainFile, harnessCode);
            }

            logger.info("Python work dir: {}", workDir);
            // docker exec python-container python3 main.py
            CommandResult runResult = runInDocker(workDir, "python3 main.py", input);

            if (runResult.isTimedOut()) {
                status = SubmissionStatus.TIMEOUT;
            } else if (!runResult.isSuccess()) {
                status = SubmissionStatus.RUNTIME_ERROR;
            }
            output.append(runResult.getOutput());
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            logger.error("Python execution failed", e);
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
