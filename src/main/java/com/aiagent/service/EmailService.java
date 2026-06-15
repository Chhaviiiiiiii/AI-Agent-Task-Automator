package com.aiagent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public void sendTaskCreatedNotification(String userEmail, String taskTitle, Long taskId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Task Created: " + taskTitle);
            message.setText(String.format(
                "Your task '%s' has been created successfully.\n" +
                "Task ID: %d\n" +
                "You can track its progress in your dashboard.",
                taskTitle, taskId
            ));
            mailSender.send(message);
            logger.info("Task creation email sent to: {}", userEmail);
        } catch (Exception e) {
            logger.error("Error sending task creation email: {}", e.getMessage());
        }
    }

    public void sendTaskCompletedNotification(String userEmail, String taskTitle, String result) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Task Completed: " + taskTitle);
            message.setText(String.format(
                "Your task '%s' has been completed!\n" +
                "Result: %s",
                taskTitle, result
            ));
            mailSender.send(message);
            logger.info("Task completion email sent to: {}", userEmail);
        } catch (Exception e) {
            logger.error("Error sending task completion email: {}", e.getMessage());
        }
    }

    public void sendTaskFailedNotification(String userEmail, String taskTitle, String errorMessage) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Task Failed: " + taskTitle);
            message.setText(String.format(
                "Your task '%s' has failed.\n" +
                "Error: %s\n" +
                "Please review and try again.",
                taskTitle, errorMessage
            ));
            mailSender.send(message);
            logger.info("Task failure email sent to: {}", userEmail);
        } catch (Exception e) {
            logger.error("Error sending task failure email: {}", e.getMessage());
        }
    }
}
