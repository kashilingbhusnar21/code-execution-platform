package com.codeplatform.code_executor.executor;

import com.codeplatform.code_executor.entity.SubmissionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JavaExecutor extends AbstractDockerExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JavaExecutor.class);

    @Override
    public String getLanguage() {
        return "java";
    }

    @Override
    public String getSourceFileName() {
        return "Main.java";
    }

    @Override
    public ExecutionResult execute(String code, String filePath, String input) {
        long startTime = System.currentTimeMillis();
        StringBuilder output = new StringBuilder();
        SubmissionStatus status = SubmissionStatus.SUCCESS;

        Path sourceFile = Paths.get(filePath).toAbsolutePath().normalize();
        Path workDir = sourceFile.getParent();
        String packageName = null;

        try {
            packageName = extractPackageName(sourceFile);
            logger.info("Java work dir: {}, package: {}", workDir, packageName);

            if (packageName != null && !packageName.isEmpty()) {
                sourceFile = moveFileToPackageStructure(workDir, sourceFile, packageName);
            }

            // docker exec java-container javac ...
            String compileCmd = buildCompileCommand(workDir, sourceFile);
            CommandResult compileResult = runInDocker(workDir, compileCmd, null);

            if (compileResult.isTimedOut()) {
                status = SubmissionStatus.TIMEOUT;
                output.append(compileResult.getOutput());
            } else if (!compileResult.isSuccess()) {
                status = SubmissionStatus.COMPILATION_ERROR;
                output.append("Compilation Error:\n").append(compileResult.getOutput());
            } else {
                // docker exec java-container java -cp . Main
                String runCmd = buildRunCommand(packageName);
                CommandResult runResult = runInDocker(workDir, runCmd, input);

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
            logger.error("Java execution failed", e);
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

    private String buildCompileCommand(Path workDir, Path sourceFile) {
        String sourceRel = workDir.relativize(sourceFile.toAbsolutePath().normalize())
                .toString()
                .replace("\\", "/");
        return "javac -d . " + sourceRel;
    }

    private String buildRunCommand(String packageName) {
        String className = (packageName != null && !packageName.isEmpty())
                ? packageName + ".Main"
                : "Main";
        return "java -cp . " + className;
    }

    private String extractPackageName(Path filePath) throws IOException {
        String firstLine = Files.lines(filePath).findFirst().orElse("").trim();
        Pattern pattern = Pattern.compile("^package\\s+([a-zA-Z_][a-zA-Z0-9_.]*);");
        Matcher matcher = pattern.matcher(firstLine);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Path moveFileToPackageStructure(Path workDir, Path sourcePath, String packageName)
            throws IOException {
        Path targetDir = workDir.resolve(packageName.replace('.', '/'));
        Files.createDirectories(targetDir);
        Path target = targetDir.resolve(sourcePath.getFileName());
        Files.move(sourcePath, target, StandardCopyOption.REPLACE_EXISTING);
        return target;
    }
}
