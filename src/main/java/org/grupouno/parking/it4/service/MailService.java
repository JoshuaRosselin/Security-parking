package org.grupouno.parking.it4.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service

public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    private JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String email , String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verification code parkingIT4");
        message.setText("This is the verification code to recover your account: " + code);
        mailSender.send(message);
        logger.info("Email send to {}, with code {}", email, code);
    }

    public void sendPasswordAndUser(String email , String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Email parkingIT4");
        message.setText("This are your credentials:\n" +
                            "your user: " + email + "\n your password: " + password);
        mailSender.send(message);
        logger.info("Email send to {}, with his password", email);
    }

}
