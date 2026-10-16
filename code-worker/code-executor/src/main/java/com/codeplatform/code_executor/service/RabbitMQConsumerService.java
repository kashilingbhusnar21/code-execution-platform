package com.codeplatform.code_executor.service;

import com.codeplatform.code_executor.config.RabbitMQConfig;
import com.codeplatform.code_executor.dto.CodeResultMessage;
import com.codeplatform.code_executor.dto.CodeSubmissionMessage;
import com.codeplatform.code_executor.entity.SubmissionStatus;
import com.codeplatform.code_executor.executor.CodeExecutor;
import com.codeplatform.code_executor.executor.ExecutorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumerService.class);

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
        logger.info("Received code submission: submissionId={}, language={}", 
                message.getSubmissionId(), message.getLanguage());

        CodeResultMessage resultMessage = new CodeResultMessage();
        resultMessage.setSubmissionId(message.getSubmissionId());

        try {
            // Validate language support
            if (executorRegistry.find(message.getLanguage()).isEmpty()) {
                String errorMsg = "Language '" + message.getLanguage() + "' is not supported. Supported: " 
                        + executorRegistry.getSupportedLanguages();
                logger.error(errorMsg);
                resultMessage.setStatus("ERROR");
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

            // Execute code synchronously (this runs in the consumer thread)
            var response = codeExecutionService.executeCodeOnly(
                    message.getCode(),
                    message.getLanguage(),
                    message.getUserId(),
                    message.getInput()
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
