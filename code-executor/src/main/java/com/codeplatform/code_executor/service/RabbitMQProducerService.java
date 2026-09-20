package com.codeplatform.code_executor.service;

import com.codeplatform.code_executor.config.RabbitMQConfig;
import com.codeplatform.code_executor.dto.CodeResultMessage;
import com.codeplatform.code_executor.dto.CodeSubmissionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducerService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducerService.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendCodeSubmission(CodeSubmissionMessage message) {
        try {
            logger.info("Sending code submission to queue: submissionId={}", message.getSubmissionId());
            amqpTemplate.convertAndSend(
                    RabbitMQConfig.CODE_EXCHANGE,
                    RabbitMQConfig.CODE_SUBMISSION_ROUTING_KEY,
                    message
            );
            logger.info("Code submission sent successfully: submissionId={}", message.getSubmissionId());
        } catch (Exception e) {
            logger.error("Failed to send code submission: submissionId={}", message.getSubmissionId(), e);
            throw new RuntimeException("Failed to send code submission to queue", e);
        }
    }

    public void sendCodeResult(CodeResultMessage message) {
        try {
            logger.info("Sending code result to queue: submissionId={}", message.getSubmissionId());
            amqpTemplate.convertAndSend(
                    RabbitMQConfig.CODE_EXCHANGE,
                    RabbitMQConfig.CODE_RESULT_ROUTING_KEY,
                    message
            );
            logger.info("Code result sent successfully: submissionId={}", message.getSubmissionId());
        } catch (Exception e) {
            logger.error("Failed to send code result: submissionId={}", message.getSubmissionId(), e);
            throw new RuntimeException("Failed to send code result to queue", e);
        }
    }
}
