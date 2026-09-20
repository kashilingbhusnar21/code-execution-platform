package com.codeplatform.code_executor.service;

import com.codeplatform.code_executor.config.RabbitMQConfig;
import com.codeplatform.code_executor.dto.CodeResultMessage;
import com.codeplatform.code_executor.entity.Submission;
import com.codeplatform.code_executor.entity.SubmissionStatus;
import com.codeplatform.code_executor.repository.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQResultConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQResultConsumerService.class);

    @Autowired
    private SubmissionRepository submissionRepository;

    @RabbitListener(queues = RabbitMQConfig.CODE_RESULT_QUEUE)
    public void handleCodeResult(CodeResultMessage result) {
        logger.info("Received code result: submissionId={}, status={}", 
                result.getSubmissionId(), result.getStatus());

        try {
            Submission submission = submissionRepository.findById(result.getSubmissionId()).orElse(null);
            
            if (submission == null) {
                logger.error("Submission not found for id: {}", result.getSubmissionId());
                return;
            }

            // Update submission with results
            submission.setOutput(result.getOutput());
            submission.setExecutionTime(result.getExecutionTimeMs());

            // Map status string to enum
            if ("SUCCESS".equals(result.getStatus())) {
                submission.setStatus(SubmissionStatus.SUCCESS);
            } else if ("ERROR".equals(result.getStatus())) {
                submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            } else if ("TIMEOUT".equals(result.getStatus())) {
                submission.setStatus(SubmissionStatus.TIMEOUT);
            } else {
                submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            }

            submissionRepository.save(submission);
            logger.info("Submission updated successfully: id={}, status={}", 
                    submission.getId(), submission.getStatus());

        } catch (Exception e) {
            logger.error("Error processing code result: submissionId={}", result.getSubmissionId(), e);
        }
    }
}
