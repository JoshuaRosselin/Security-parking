package org.grupouno.parking.it4.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service

public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    private JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Verification Code - ParkingIT4");

            String htmlContent = """
        <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #1e1e1e; 
                        color: #f4f4f4; 
                        padding: 20px;
                        margin: 0;
                    }
                    .container {
                        background-color: #282828;
                        border-radius: 8px;
                        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
                        max-width: 600px;
                        margin: auto;
                        padding: 30px;
                    }
                    h1 {
                        color: #007BFF;
                        text-align: center;
                    }
                    .code {
                        font-size: 28px;
                        color: #ffffff; 
                        font-weight: bold;
                        background-color: #007BFF; 
                        border: 1px solid #007BFF;
                        border-radius: 4px;
                        padding: 10px;
                        text-align: center;
                        display: inline-block;
                        margin: 20px 0;
                    }
                    p {
                        color: #f4f4f4; 
                        line-height: 1.6;
                    }
                    .footer {
                        margin-top: 30px;
                        font-size: 12px;
                        color: #bbb; 
                        text-align: center;
                    }
                    .footer a {
                        color: #007BFF;
                        text-decoration: none;
                    }
                    .footer a:hover {
                        text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Verification Code</h1>
                    <p>Hello,</p>
                    <p>You have requested to recover your account. Please use the following verification code to proceed:</p>
                    <p class="code">%s</p>
                    <p>If you did not request this, please ignore this email.</p>
                    <div class="footer">
                        <p>Thank you,<br>ParkingIT4</p>
                    </div>
                </div>
            </body>
        </html>
        """;
            String formattedHtmlContent = String.format(htmlContent, code);
            helper.setText(formattedHtmlContent, true);
            mailSender.send(message);
            logger.info("HTML email sent to {}, with code {}", email, code);
        } catch (MessagingException e) {
            logger.error("Error while sending verification code email", e);
        }
    }

    public void sendPasswordAndUser(String email, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Your Account Credentials - ParkingIT4");

            String htmlContent = """
        <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #1e1e1e; 
                        color: #f4f4f4; 
                        padding: 20px;
                        margin: 0;
                    }
                    .container {
                        background-color: #282828;
                        border-radius: 8px;
                        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
                        max-width: 600px;
                        margin: auto;
                        padding: 30px;
                    }
                    h1 {
                        color: #007BFF;
                        text-align: center;
                    }
                    .credentials {
                        font-size: 18px;
                        color: #ffffff;
                    }
                    p {
                        color: #ffffff; 
                        line-height: 1.6; 
                    }
                    .footer {
                        margin-top: 30px;
                        font-size: 12px;
                        color: #bbb; 
                        text-align: center;
                    }
                    .footer a {
                        color: #007BFF;
                        text-decoration: none;
                    }
                    .footer a:hover {
                        text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Your Account Credentials</h1>
                    <p>Hello,</p>
                    <p class="credentials">Here are your account details:</p>
                    <p class="credentials"><strong>Email:</strong> %s</p> 
                    <p class="credentials"><strong>Password:</strong> %s</p> 
                    <p>Please keep this information secure. If you did not request this, please contact support.</p>
                    <div class="footer">
                        <p>Thank you,<br>ParkingIT4 Team</p>
                    </div>
                </div>
            </body>
        </html>
        """;
            String formattedHtmlContent = String.format(htmlContent, email, password);
            helper.setText(formattedHtmlContent, true);
            mailSender.send(message);
            logger.info("HTML email sent to {}, with their credentials", email);
        } catch (MessagingException e) {
            logger.error("Error while sending email", e);
        }
    }

}