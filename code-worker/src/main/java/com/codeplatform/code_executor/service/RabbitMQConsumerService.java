package com.codeplatform.code_executor.service;

import com.codeplatform.code_executor.config.RabbitMQConfig;
import com.codeplatform.code_executor.dto.CodeResultMessage;
import com.codeplatform.code_executor.dto.CodeSubmissionMessage;
import com.codeplatform.code_executor.dto.GradingResult;
import com.codeplatform.code_executor.executor.ExecutorRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumerService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CodeExecutionService codeExecutionService;

    @Autowired
    private ExecutorRegistry executorRegistry;

    @Autowired
    private ContainerManagerService containerManager;

    @Autowired
    private RabbitMQProducerService producerService;

    @RabbitListener(queues = RabbitMQConfig.CODE_SUBMISSION_QUEUE)
    public void handleCodeSubmission(CodeSubmissionMessage message) {
        logger.info("Received code submission: submissionId={}, mode={}, language={}", 
                message.getSubmissionId(), message.getMode(), message.getLanguage());

        CodeResultMessage resultMessage = new CodeResultMessage();
        resultMessage.setSubmissionId(message.getSubmissionId());

        try {
            // Validate language support
            if (executorRegistry.find(message.getLanguage()).isEmpty()) {
                String errorMsg = "Language '" + message.getLanguage() + "' is not supported. Supported: " 
                        + executorRegistry.getSupportedLanguages();
                logger.error(errorMsg);
                resultMessage.setStatus("COMPILE_ERROR");
                resultMessage.setErrorMessage(errorMsg);
                resultMessage.setExecutionTimeMs(0L);
                producerService.sendCodeResult(resultMessage);
                return;
            }

            // Ensure container is running
            try {
                containerManager.ensureContainerRunning(message.getLanguage());
            } catch (Exception e) {
                String errorMsg = "Execution environment for '" + message.getLanguage() + "' is unavailable: " + e.getMessage();
                logger.error(errorMsg);
                resultMessage.setStatus("ERROR");
                resultMessage.setErrorMessage(errorMsg);
                resultMessage.setExecutionTimeMs(0L);
                producerService.sendCodeResult(resultMessage);
                return;
            }

            // SUBMISSION MODE: Evaluate against all test cases
            if ("SUBMIT".equalsIgnoreCase(message.getMode()) && message.getTestCases() != null && !message.getTestCases().isEmpty()) {
                logger.info("Handling full test-case submission evaluation for submissionId={}", message.getSubmissionId());
                GradingResult grading = codeExecutionService.evaluateSubmission(
                        message.getCode(),
                        message.getLanguage(),
                        message.getUserId(),
                        message.getProblemTitle(),
                        message.getTestCases(),
                        message.getSubmissionId()
                );

                resultMessage.setStatus(grading.getStatus().name());
                resultMessage.setPassedCount(grading.getPassedCount());
                resultMessage.setTotalCount(grading.getTotalCount());
                resultMessage.setExecutionTimeMs(grading.getTotalExecutionTimeMs());
                resultMessage.setErrorMessage(grading.getErrorDetails());
                resultMessage.setTestCaseResults(grading.getTestCaseResults());

                try {
                    resultMessage.setOutput(objectMapper.writeValueAsString(grading.getTestCaseResults()));
                } catch (Exception e) {
                    resultMessage.setOutput(grading.getStatus().name());
                }

                logger.info("Submission evaluation completed: submissionId={}, verdict={}, passed={}/{}, time={}ms",
                        message.getSubmissionId(), grading.getStatus(), grading.getPassedCount(),
                        grading.getTotalCount(), grading.getTotalExecutionTimeMs());

                producerService.sendCodeResult(resultMessage);
                return;
            }

            // RUN MODE: Execute against single input (custom or sample testcase)
            var response = codeExecutionService.executeCodeOnly(
                    message.getCode(),
                    message.getLanguage(),
                    message.getUserId(),
                    message.getInput(),
                    message.getProblemTitle()
            );

            // Build result message
            resultMessage.setOutput(response.getOutput());
            resultMessage.setStdout(response.getStdout());
            resultMessage.setStderr(response.getStderr());
            resultMessage.setStatus(response.getStatus());
            resultMessage.setExecutionTimeMs(response.getExecutionTimeMs());
            resultMessage.setErrorMessage(null);

            logger.info("Code execution completed: submissionId={}, status={}, time={}ms",
                    message.getSubmissionId(), response.getStatus(), response.getExecutionTimeMs());

        } catch (Exception e) {
            logger.error("Error processing code submission: submissionId={}", message.getSubmissionId(), e);
            resultMessage.setStatus("ERROR");
            resultMessage.setErrorMessage("Execution failed: " + e.getMessage());
            resultMessage.setExecutionTimeMs(0L);
        }

        // Send result back to result queue
        producerService.sendCodeResult(resultMessage);
    }
}
