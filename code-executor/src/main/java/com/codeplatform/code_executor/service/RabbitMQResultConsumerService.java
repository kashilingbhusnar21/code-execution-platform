package com.codeplatform.code_executor.service;

import com.codeplatform.code_executor.config.RabbitMQConfig;
import com.codeplatform.code_executor.dto.CodeResultMessage;
import com.codeplatform.code_executor.entity.Submission;
import com.codeplatform.code_executor.entity.SubmissionStatus;
import com.codeplatform.code_executor.entity.User;
import com.codeplatform.code_executor.repository.SubmissionRepository;
import com.codeplatform.code_executor.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RabbitMQResultConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQResultConsumerService.class);

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

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

            // Update user streak
            updateUserStreak(submission.getUserId());

        } catch (Exception e) {
            logger.error("Error processing code result: submissionId={}", result.getSubmissionId(), e);
        }
    }

    private void updateUserStreak(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                logger.error("User not found for id: {}", userId);
                return;
            }

            LocalDate today = LocalDate.now();
            LocalDate lastSubmission = user.getLastSubmissionDate();

            if (lastSubmission == null) {
                // First submission
                user.setStreakCount(1);
                user.setLastSubmissionDate(today);
            } else if (lastSubmission.equals(today)) {
                // Already submitted today, no change
                logger.info("User {} already submitted today, streak unchanged: {}", userId, user.getStreakCount());
                return;
            } else if (lastSubmission.equals(today.minusDays(1))) {
                // Submitted yesterday, increment streak
                user.setStreakCount(user.getStreakCount() + 1);
                user.setLastSubmissionDate(today);
                logger.info("User {} streak incremented to {}", userId, user.getStreakCount());
            } else {
                // Streak broken, reset to 1
                user.setStreakCount(1);
                user.setLastSubmissionDate(today);
                logger.info("User {} streak reset to 1 (last submission was {})", userId, lastSubmission);
            }

            userRepository.save(user);
            logger.info("User streak updated: userId={}, streak={}", userId, user.getStreakCount());

        } catch (Exception e) {
            logger.error("Error updating user streak for userId={}", userId, e);
        }
    }
}
