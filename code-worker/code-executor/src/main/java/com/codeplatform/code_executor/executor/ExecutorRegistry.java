package com.codeplatform.code_executor.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Permanent language → executor registry.
 *
 * Spring's {@code Map<String, CodeExecutor>} injection keys by bean name
 * (e.g. "javaExecutor"), NOT language id ("java"). Always resolve executors
 * through this registry so new language executors keep working automatically.
 *
 * To add a language: create a {@code @Component} implementing {@link CodeExecutor}
 * and return the language id from {@link CodeExecutor#getLanguage()}.
 */
@Component
public class ExecutorRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorRegistry.class);

    private final Map<String, CodeExecutor> executorsByLanguage = new ConcurrentHashMap<>();

    public ExecutorRegistry(List<CodeExecutor> executors) {
        if (executors == null || executors.isEmpty()) {
            throw new IllegalStateException(
                    "No CodeExecutor beans found. Add at least one @Component implementing CodeExecutor.");
        }

        for (CodeExecutor executor : executors) {
            String language = normalize(executor.getLanguage());
            if (language == null || language.isEmpty()) {
                throw new IllegalStateException(
                        "CodeExecutor " + executor.getClass().getName() + " returned empty getLanguage().");
            }

            CodeExecutor previous = executorsByLanguage.put(language, executor);
            if (previous != null) {
                throw new IllegalStateException(
                        "Duplicate CodeExecutor for language '" + language + "': "
                                + previous.getClass().getName() + " and " + executor.getClass().getName());
            }
        }

        logger.info("ExecutorRegistry ready. Supported languages: {}", executorsByLanguage.keySet());
    }

    public Optional<CodeExecutor> find(String language) {
        String key = normalize(language);
        if (key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(executorsByLanguage.get(key));
    }

    public CodeExecutor getRequired(String language) {
        return find(language).orElseThrow(() -> new IllegalArgumentException(
                "Language '" + language + "' is not supported. Supported: " + getSupportedLanguages()));
    }

    public boolean supports(String language) {
        return find(language).isPresent();
    }

    public Set<String> getSupportedLanguages() {
        return Collections.unmodifiableSet(executorsByLanguage.keySet());
    }

    private static String normalize(String language) {
        if (language == null) {
            return null;
        }
        return language.trim().toLowerCase(Locale.ROOT);
    }
}
