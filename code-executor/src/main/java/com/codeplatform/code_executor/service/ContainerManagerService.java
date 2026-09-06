package com.codeplatform.code_executor.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Keeps one warm Docker container per language and runs code via {@code docker exec}.
 * Avoids the 3–5s cold-start cost of {@code docker run --rm} on every request.
 */
@Service
public class ContainerManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ContainerManagerService.class);

    public static final Path HOST_TEMP_DIR = Paths.get("C:\\temp");
    private static final long COMMAND_TIMEOUT_SECONDS = 30;
    private static final long START_TIMEOUT_SECONDS = 180;

    /** language → container name */
    private final Map<String, String> languageToContainer = new ConcurrentHashMap<>();

    /** language → image */
    private final Map<String, String> languageToImage = new LinkedHashMap<>();

    /** per-language lock for start/restart (exec itself can run concurrently) */
    private final Map<String, ReentrantLock> languageLocks = new ConcurrentHashMap<>();

    @PostConstruct
    public void startAllContainers() {
        languageToImage.put("java", "eclipse-temurin:17-jdk");
        languageToImage.put("python", "python:3.10");
        languageToImage.put("cpp", "gcc:latest");

        languageToContainer.put("java", "java-container");
        languageToContainer.put("python", "python-container");
        languageToContainer.put("cpp", "cpp-container");

        for (String language : languageToContainer.keySet()) {
            languageLocks.put(language, new ReentrantLock());
        }

        ensureHostTempDir();

        logger.info("Starting warm execution containers...");
        for (String language : languageToContainer.keySet()) {
            try {
                ensureContainerRunning(language);
                logger.info("Warm container ready: {} → {}", language, languageToContainer.get(language));
            } catch (Exception e) {
                logger.error("Failed to start warm container for {}: {}", language, e.getMessage(), e);
            }
        }
    }

    @PreDestroy
    public void shutdownContainers() {
        logger.info("Shutting down warm execution containers...");
        for (String containerName : new ArrayList<>(languageToContainer.values())) {
            try {
                runDocker(List.of("docker", "stop", containerName), null, 60);
                runDocker(List.of("docker", "rm", "-f", containerName), null, 30);
                logger.info("Removed container: {}", containerName);
            } catch (Exception e) {
                logger.warn("Failed to remove container {}: {}", containerName, e.getMessage());
            }
        }
        languageToContainer.clear();
    }

    public String getContainerName(String language) {
        return languageToContainer.get(normalize(language));
    }

    /**
     * Ensures the language container is running, then executes a command inside it.
     */
    public ExecResult exec(String language, Path workDir, String command, String stdin)
            throws IOException, InterruptedException {
        String lang = normalize(language);
        ensureContainerRunning(lang);

        String containerName = languageToContainer.get(lang);
        if (containerName == null) {
            throw new IllegalArgumentException("No warm container configured for language: " + language);
        }

        String workRel = toDockerRelative(workDir);
        List<String> args = List.of(
                "docker", "exec",
                "-i",
                "-w", "/app/" + workRel,
                containerName,
                "sh", "-c",
                command
        );

        logger.info("docker exec [{}]: {}", lang, String.join(" ", args));
        return runDocker(args, stdin, COMMAND_TIMEOUT_SECONDS);
    }

    public void ensureContainerRunning(String language) throws IOException, InterruptedException {
        String lang = normalize(language);
        ReentrantLock lock = languageLocks.computeIfAbsent(lang, k -> new ReentrantLock());
        lock.lock();
        try {
            String containerName = languageToContainer.get(lang);
            String image = languageToImage.get(lang);
            if (containerName == null || image == null) {
                throw new IllegalArgumentException("Unsupported language for warm container: " + language);
            }

            if (isContainerRunning(containerName)) {
                return;
            }

            if (containerExists(containerName)) {
                logger.warn("Container {} exists but is not running — starting it", containerName);
                ExecResult startResult = runDocker(List.of("docker", "start", containerName), null, START_TIMEOUT_SECONDS);
                if (!startResult.isSuccess() || !isContainerRunning(containerName)) {
                    logger.warn("docker start failed for {}, recreating. output={}",
                            containerName, startResult.getOutput());
                    runDocker(List.of("docker", "rm", "-f", containerName), null, 30);
                    createAndStartContainer(containerName, image);
                }
            } else {
                createAndStartContainer(containerName, image);
            }

            if (!isContainerRunning(containerName)) {
                throw new IOException("Failed to start warm container: " + containerName);
            }
        } finally {
            lock.unlock();
        }
    }

    private void createAndStartContainer(String containerName, String image)
            throws IOException, InterruptedException {
        ensureHostTempDir();

        List<String> createArgs = Arrays.asList(
                "docker", "run", "-dit",
                "--name", containerName,
                "--memory=256m",
                "--cpus=1",
                "--pids-limit=50",
                "--network=none",
                "-v", HOST_TEMP_DIR.toAbsolutePath() + ":/app",
                "-w", "/app",
                image,
                "sleep", "infinity"
        );

        logger.info("Creating warm container: {}", String.join(" ", createArgs));
        ExecResult result = runDocker(createArgs, null, START_TIMEOUT_SECONDS);
        if (!result.isSuccess()) {
            logger.warn("Create failed for {}, retrying after rm -f. output={}",
                    containerName, result.getOutput());
            runDocker(List.of("docker", "rm", "-f", containerName), null, 30);
            result = runDocker(createArgs, null, START_TIMEOUT_SECONDS);
            if (!result.isSuccess()) {
                throw new IOException("Unable to create container " + containerName + ": " + result.getOutput());
            }
        }
    }

    private boolean isContainerRunning(String containerName) throws IOException, InterruptedException {
        ExecResult result = runDocker(
                List.of("docker", "inspect", "-f", "{{.State.Running}}", containerName),
                null,
                15
        );
        return result.isSuccess() && result.getOutput().trim().equalsIgnoreCase("true");
    }

    private boolean containerExists(String containerName) throws IOException, InterruptedException {
        ExecResult result = runDocker(
                List.of("docker", "inspect", containerName),
                null,
                15
        );
        return result.isSuccess();
    }

    private void ensureHostTempDir() {
        try {
            Files.createDirectories(HOST_TEMP_DIR);
        } catch (IOException e) {
            logger.warn("Could not create host temp dir {}: {}", HOST_TEMP_DIR, e.getMessage());
        }
    }

    public String toDockerRelative(Path absolutePath) {
        Path base = HOST_TEMP_DIR.toAbsolutePath().normalize();
        Path abs = absolutePath.toAbsolutePath().normalize();
        return base.relativize(abs).toString().replace("\\", "/");
    }

    private ExecResult runDocker(List<String> args, String stdin, long timeoutSeconds)
            throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        if (stdin != null && !stdin.isEmpty()) {
            try (OutputStream outputStream = process.getOutputStream()) {
                outputStream.write(stdin.getBytes(StandardCharsets.UTF_8));
                if (!stdin.endsWith("\n")) {
                    outputStream.write('\n');
                }
                outputStream.flush();
            }
        } else {
            process.getOutputStream().close();
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            return ExecResult.timeout(timeoutSeconds);
        }

        return new ExecResult(output.toString(), process.exitValue(), false);
    }

    private static String normalize(String language) {
        if (language == null) {
            return "";
        }
        return language.trim().toLowerCase(Locale.ROOT);
    }

    public static class ExecResult {
        private final String output;
        private final int exitCode;
        private final boolean timedOut;

        ExecResult(String output, int exitCode, boolean timedOut) {
            this.output = output;
            this.exitCode = exitCode;
            this.timedOut = timedOut;
        }

        static ExecResult timeout(long seconds) {
            return new ExecResult(
                    "Error: Execution timeout after " + seconds + " seconds",
                    -1,
                    true
            );
        }

        public String getOutput() {
            return output == null ? "" : output;
        }

        public int getExitCode() {
            return exitCode;
        }

        public boolean isTimedOut() {
            return timedOut;
        }

        public boolean isSuccess() {
            return !timedOut && exitCode == 0;
        }
    }
}
