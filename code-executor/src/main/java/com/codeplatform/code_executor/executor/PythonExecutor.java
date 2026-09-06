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
        long startTime = System.currentTimeMillis();
        StringBuilder output = new StringBuilder();
        SubmissionStatus status = SubmissionStatus.SUCCESS;

        Path sourceFile = Paths.get(filePath).toAbsolutePath().normalize();
        Path workDir = sourceFile.getParent();

        try {
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
