package org.grupouno.parking.it4.service;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    private JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender, AudithService audithService) {
        this.mailSender = mailSender;
        this.audithService = audithService;
    }

    public void sendVerificationCode(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verification code parkingIT4");
        message.setText("This is the verification code to recover your account: " + code);
        mailSender.send(message);
    }

    private void auditAction(String entity, String description, String operation,
                             Map<String, Object> request, Map<String, Object> response, String result) {
        try {
            audithService.createAudit(entity, description, operation, request, response, result);
        } catch (Exception e) {
            System.err.println("Error saving audit record: " + e.getMessage());
        }
    }
}
