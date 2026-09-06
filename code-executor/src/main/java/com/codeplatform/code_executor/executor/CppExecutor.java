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
        long startTime = System.currentTimeMillis();
        StringBuilder output = new StringBuilder();
        SubmissionStatus status = SubmissionStatus.SUCCESS;

        Path sourceFile = Paths.get(filePath).toAbsolutePath().normalize();
        Path workDir = sourceFile.getParent();

        try {
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
