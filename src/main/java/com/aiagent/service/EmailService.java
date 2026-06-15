package com.aiagent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendTaskExecutionEmail(String toEmail, String taskTitle, String aiResponse) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("noreply@taskautomator.com");
            helper.setTo(toEmail);
            helper.setSubject("[AI Task Automator] " + taskTitle);

            String htmlBody = buildEmailBody(taskTitle, aiResponse);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildEmailBody(String taskTitle, String aiResponse) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><style>body{font-family:Arial,sans-serif;background:#f5f5f5;}" +
                ".container{max-width:600px;margin:0 auto;background:white;padding:20px;border-radius:10px;}" +
                ".header{color:#6C63FF;font-size:24px;font-weight:bold;margin-bottom:20px;}" +
                ".content{color:#333;line-height:1.6;}" +
                ".ai-response{background:#EEF0FF;padding:15px;border-left:4px solid #6C63FF;margin-top:15px;border-radius:5px;}" +
                ".footer{color:#999;font-size:12px;margin-top:30px;border-top:1px solid #eee;padding-top:20px;}" +
                "</style></head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>🤖 Task Automation Complete</div>" +
                "<div class='content'>" +
                "<p>Hi there,</p>" +
                "<p>Your task '<strong>" + taskTitle + "</strong>' has been processed by AI Agent.</p>" +
                "<div class='ai-response'>" +
                "<strong>Action Plan:</strong><br/>" +
                aiResponse +
                "</div>" +
                "<p>You can view more details in your dashboard.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "© 2026 AI Task Automator. All rights reserved." +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
