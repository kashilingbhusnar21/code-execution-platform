package com.codeplatform.code_executor.service;

import com.codeplatform.code_executor.dto.GradingResult;
import com.codeplatform.code_executor.dto.RunResponse;
import com.codeplatform.code_executor.dto.TestCaseItem;
import com.codeplatform.code_executor.dto.TestCaseResultItem;
import com.codeplatform.code_executor.entity.SubmissionStatus;
import com.codeplatform.code_executor.executor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CodeExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(CodeExecutionService.class);
    private static final Path BASE_FOLDER = ContainerManagerService.HOST_TEMP_DIR;

    @Autowired
    private ExecutorRegistry executorRegistry;

    @Autowired
    private ContainerManagerService containerManager;

    public RunResponse executeCodeOnly(String code, String language, Long userId, String input) {
        return executeCodeOnly(code, language, userId, input, null);
    }

    public RunResponse executeCodeOnly(String code, String language, Long userId, String input, String problemTitle) {
        logger.info("Executing code only (no DB save): language={}, userId={}, problemTitle={}", language, userId, problemTitle);

        boolean isProblem = DsaProblemType.isProblemMode(problemTitle, code);
        DsaProblemDef problemDef = isProblem ? DsaProblemType.from(problemTitle, code) : null;
        if (isProblem && problemDef == null) {
            String unsupportedMsg = "Error: Unsupported problem type: '" + (problemTitle != null ? problemTitle : "unknown")
                    + "'. Automated harness is not configured for this problem.";
            logger.warn(unsupportedMsg);
            return RunResponse.error(unsupportedMsg);
        }

        Optional<CodeExecutor> executorOpt = executorRegistry.find(language);
        if (executorOpt.isEmpty()) {
            logger.error("No executor found for language: {}. Supported: {}",
                    language, executorRegistry.getSupportedLanguages());
            return RunResponse.error(
                    "Error: Language '" + language + "' is not supported. Supported: "
                            + executorRegistry.getSupportedLanguages());
        }

        try {
            containerManager.ensureContainerRunning(language);
        } catch (Exception e) {
            logger.error("Warm container unavailable for {}: {}", language, e.getMessage());
            return RunResponse.error(
                    "Error: execution environment for '" + language + "' is unavailable: " + e.getMessage());
        }

        CodeExecutor executor = executorOpt.get();
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String uniqueFolder = userId + "_" + timestamp;
        String fileName = executor.getSourceFileName();
        String filePath = BASE_FOLDER.resolve(uniqueFolder).resolve(fileName).toString();

        try {
            Path tempDir = BASE_FOLDER.resolve(uniqueFolder);
            Files.createDirectories(tempDir);
            Files.write(Paths.get(filePath), code.getBytes(StandardCharsets.UTF_8));
            logger.info("Code saved to temp file: {}", filePath);

            CodeExecutor.ExecutionResult result = executor.execute(code, filePath, input, problemTitle);
            logger.info("Code execution completed: status={}, time={}ms",
                    result.getStatus(), result.getExecutionTimeMs());

            return RunResponse.from(result.getOutput(), result.getStatus(), result.getExecutionTimeMs());

        } catch (IOException e) {
            logger.error("IO error during code execution", e);
            return RunResponse.from("Error executing code: " + e.getMessage(), SubmissionStatus.RUNTIME_ERROR, 0L);
        }
    }

    public GradingResult evaluateSubmission(
            String code,
            String language,
            Long userId,
            String problemTitle,
            List<TestCaseItem> testCases,
            Long submissionId
    ) {
        logger.info("Evaluating submission: id={}, language={}, userId={}, problemTitle={}, testCasesCount={}",
                submissionId, language, userId, problemTitle, testCases != null ? testCases.size() : 0);

        if (testCases == null || testCases.isEmpty()) {
            return new GradingResult(SubmissionStatus.ERROR, 0, 0, 0L, "No test cases provided", Collections.emptyList());
        }

        String langLower = language.toLowerCase();
        Optional<CodeExecutor> executorOpt = executorRegistry.find(langLower);
        if (executorOpt.isEmpty()) {
            String msg = "Language '" + language + "' is not supported.";
            return new GradingResult(SubmissionStatus.COMPILE_ERROR, 0, testCases.size(), 0L, msg, Collections.emptyList());
        }

        try {
            containerManager.ensureContainerRunning(langLower);
        } catch (Exception e) {
            String msg = "Execution environment for '" + language + "' is unavailable: " + e.getMessage();
            return new GradingResult(SubmissionStatus.ERROR, 0, testCases.size(), 0L, msg, Collections.emptyList());
        }

        String uniqueFolder = "sub_" + (userId != null ? userId : 0) + "_" + (submissionId != null ? submissionId : 0)
                + "_" + Instant.now().toEpochMilli();
        Path workDir = BASE_FOLDER.resolve(uniqueFolder);

        try {
            Files.createDirectories(workDir);

            boolean isProblem = DsaProblemType.isProblemMode(problemTitle, code);
            DsaProblemDef problemDef = isProblem ? DsaProblemType.from(problemTitle, code) : null;

            if (isProblem && problemDef == null) {
                String unsupportedMsg = "Unsupported problem type: '" + (problemTitle != null ? problemTitle : "unknown")
                        + "'. Automated harness is not configured for this problem.";
                logger.warn(unsupportedMsg);
                return new GradingResult(SubmissionStatus.ERROR, 0, testCases.size(), 0L, unsupportedMsg, Collections.emptyList());
            }

            String compileCmd = null;
            String runCmd;

            // Prepare source files based on language and mode
            if ("java".equals(langLower)) {
                if (problemDef != null) {
                    Path solutionFile = workDir.resolve("Solution.java");
                    String solutionContent = code;
                    if (!solutionContent.contains("import java.util")) {
                        solutionContent = "import java.util.*;\n" + solutionContent;
                    }
                    Files.writeString(solutionFile, solutionContent);

                    Path mainFile = workDir.resolve("Main.java");
                    String harnessCode = DsaHarnessGenerator.getJavaHarness(problemDef);
                    Files.writeString(mainFile, harnessCode);

                    compileCmd = "javac -d . Solution.java Main.java";
                } else {
                    Path mainFile = workDir.resolve("Main.java");
                    Files.writeString(mainFile, code);
                    compileCmd = "javac -d . Main.java";
                }
                runCmd = "java -cp . Main";
            } else if ("cpp".equals(langLower)) {
                if (problemDef != null) {
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
                    Files.writeString(solutionHeader, solutionContent);

                    Path mainFile = workDir.resolve("main.cpp");
                    String harnessCode = DsaHarnessGenerator.getCppHarness(problemDef);
                    Files.writeString(mainFile, harnessCode);
                } else {
                    Path mainFile = workDir.resolve("main.cpp");
                    Files.writeString(mainFile, code);
                }
                compileCmd = "g++ main.cpp -o main";
                runCmd = "./main";
            } else if ("python".equals(langLower)) {
                if (problemDef != null) {
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
                    Files.writeString(solutionFile, solutionContent);

                    Path mainFile = workDir.resolve("main.py");
                    String harnessCode = DsaHarnessGenerator.getPythonHarness(problemDef);
                    Files.writeString(mainFile, harnessCode);
                } else {
                    Path mainFile = workDir.resolve("main.py");
                    Files.writeString(mainFile, code);
                }
                compileCmd = null; // Python is interpreted
                runCmd = "python3 main.py";
            } else {
                return new GradingResult(SubmissionStatus.COMPILE_ERROR, 0, testCases.size(), 0L,
                        "Unsupported language: " + language, Collections.emptyList());
            }

            // Compilation step if needed
            if (compileCmd != null) {
                ContainerManagerService.ExecResult compileResult = containerManager.exec(langLower, workDir, compileCmd, null);
                if (compileResult.isTimedOut()) {
                    return new GradingResult(SubmissionStatus.TIME_LIMIT_EXCEEDED, 0, testCases.size(),
                            0L, "Compilation timed out.", Collections.emptyList());
                }
                if (compileResult.getExitCode() != 0) {
                    String compOutput = compileResult.getOutput() == null ? "Compilation failed" : compileResult.getOutput();
                    return new GradingResult(SubmissionStatus.COMPILE_ERROR, 0, testCases.size(),
                            0L, compOutput, Collections.emptyList());
                }
            }

            // Test case execution loop
            List<TestCaseResultItem> results = new ArrayList<>();
            int passedCount = 0;
            long totalExecutionTimeMs = 0L;
            SubmissionStatus finalStatus = SubmissionStatus.ACCEPTED;
            String errorDetails = null;

            for (int i = 0; i < testCases.size(); i++) {
                TestCaseItem tc = testCases.get(i);
                int tcNumber = i + 1;
                long start = System.currentTimeMillis();

                ContainerManagerService.ExecResult runResult = containerManager.exec(langLower, workDir, runCmd, tc.getInput());
                long tcDuration = System.currentTimeMillis() - start;
                totalExecutionTimeMs += tcDuration;

                TestCaseResultItem item = new TestCaseResultItem();
                item.setId(tc.getId());
                item.setTestCaseNumber(tcNumber);
                item.setIsSample(tc.getIsSample());
                item.setIsHidden(tc.getIsHidden());
                item.setExecutionTimeMs(tcDuration);

                boolean isPublicSample = Boolean.TRUE.equals(tc.getIsSample()) && !Boolean.TRUE.equals(tc.getIsHidden());
                if (isPublicSample) {
                    item.setInput(tc.getInput());
                    item.setExpectedOutput(tc.getExpectedOutput());
                }

                if (runResult.isTimedOut()) {
                    item.setPassed(false);
                    item.setStatus("TIME_LIMIT_EXCEEDED");
                    item.setFailureReason("Time Limit Exceeded");
                    finalStatus = SubmissionStatus.TIME_LIMIT_EXCEEDED;
                    results.add(item);
                    break; // Early exit on timeout
                }

                String actualOutput = runResult.getOutput() == null ? "" : runResult.getOutput().trim();

                // Check Python syntax/indentation error on first test case
                if (i == 0 && "python".equals(langLower) &&
                        (actualOutput.contains("SyntaxError:") || actualOutput.contains("IndentationError:"))) {
                    item.setPassed(false);
                    item.setStatus("COMPILE_ERROR");
                    item.setFailureReason("Syntax Error");
                    finalStatus = SubmissionStatus.COMPILE_ERROR;
                    errorDetails = actualOutput;
                    results.add(item);
                    break;
                }

                if (runResult.getExitCode() != 0) {
                    item.setPassed(false);
                    item.setStatus("RUNTIME_ERROR");
                    item.setFailureReason("Runtime Error");
                    if (isPublicSample) {
                        item.setActualOutput(actualOutput);
                    }
                    finalStatus = SubmissionStatus.RUNTIME_ERROR;
                    errorDetails = actualOutput;
                    results.add(item);
                    break; // Early exit on runtime error
                }

                if (isPublicSample) {
                    item.setActualOutput(actualOutput);
                }

                boolean matches = OutputNormalizer.matches(actualOutput, tc.getExpectedOutput());
                if (matches) {
                    item.setPassed(true);
                    item.setStatus("PASSED");
                    passedCount++;
                    results.add(item);
                } else {
                    item.setPassed(false);
                    item.setStatus("WRONG_ANSWER");
                    item.setFailureReason("Wrong Answer");
                    finalStatus = SubmissionStatus.WRONG_ANSWER;
                    results.add(item);
                    break; // Early exit on Wrong Answer
                }
            }

            return new GradingResult(finalStatus, passedCount, testCases.size(),
                    totalExecutionTimeMs, errorDetails, results);

        } catch (Exception e) {
            logger.error("Error evaluating submission id=" + submissionId, e);
            return new GradingResult(SubmissionStatus.ERROR, 0, testCases.size(),
                    0L, "Evaluation failed: " + e.getMessage(), Collections.emptyList());
        } finally {
            cleanupDirectory(workDir);
        }
    }

    private void cleanupDirectory(Path path) {
        try {
            if (path != null && Files.exists(path)) {
                Files.walk(path)
                        .sorted((a, b) -> -a.compareTo(b))
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException ignored) {
                            }
                        });
                logger.info("Cleaned up submission work directory: {}", path);
            }
        } catch (Exception e) {
            logger.warn("Failed to cleanup work directory: {}", path, e);
        }
    }
}
