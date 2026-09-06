package com.codeplatform.code_executor.executor;

import com.codeplatform.code_executor.service.ContainerManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shared helpers for language executors using warm containers ({@code docker exec}).
 */
public abstract class AbstractDockerExecutor implements CodeExecutor {

    protected static final Path BASE_FOLDER = ContainerManagerService.HOST_TEMP_DIR;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ContainerManagerService containerManager;

    /**
     * Runs a command inside the warm language container with cwd = unique work folder.
     */
    protected CommandResult runInDocker(Path workDir, String command, String stdin)
            throws IOException, InterruptedException {
        ContainerManagerService.ExecResult result =
                containerManager.exec(getLanguage(), workDir, command, stdin);
        return new CommandResult(result.getOutput(), result.getExitCode(), result.isTimedOut());
    }

    protected String toDockerRelative(Path absolutePath) {
        return containerManager.toDockerRelative(absolutePath);
    }

    protected void cleanupWorkDir(Path workDir) {
        try {
            if (workDir != null && Files.exists(workDir)) {
                deleteDirectory(workDir);
                logger.info("Deleted unique execution directory: {}", workDir);
            }
        } catch (IOException e) {
            logger.warn("Failed to cleanup temp files", e);
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        Files.walk(path)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        logger.warn("Failed to delete: {}", p);
                    }
                });
    }

    protected static class CommandResult {
        private final String output;
        private final int exitCode;
        private final boolean timedOut;

        CommandResult(String output, int exitCode, boolean timedOut) {
            this.output = output;
            this.exitCode = exitCode;
            this.timedOut = timedOut;
        }

        String getOutput() {
            return output == null ? "" : output;
        }

        int getExitCode() {
            return exitCode;
        }

        boolean isTimedOut() {
            return timedOut;
        }

        boolean isSuccess() {
            return !timedOut && exitCode == 0;
        }
    }
}
