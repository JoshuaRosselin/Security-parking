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
            helper.setSubject("Tus Credenciales de Cuenta - ParkingIT4");

            String htmlContent = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #F5F5F5;
                        color: #31363F;
                        margin: 0;
                        padding: 0;
                    }
                    .email-container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: #FFFFFF;
                        border: 1px solid #DADADA;
                        border-radius: 8px;
                        overflow: hidden;
                    }
                    .header {
                        background-color: #76ABAE;
                        color: #FFFFFF;
                        padding: 20px;
                        text-align: center;
                        font-size: 24px;
                    }
                    .content {
                        padding: 20px;
                        line-height: 1.6;
                    }
                    .footer {
                        background-color: #F5F5F5;
                        color: #999999;
                        text-align: center;
                        padding: 10px;
                        font-size: 12px;
                    }
                    .button {
                        display: inline-block;
                        background-color: #76ABAE;
                        color: #FFFFFF;
                        padding: 10px 20px;
                        text-decoration: none;
                        border-radius: 5px;
                        margin-top: 20px;
                    }
                    .info {
                        background-color: #DADADA;
                        padding: 15px;
                        border-radius: 5px;
                        margin-top: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        ParkingIT4 Team
                    </div>
                    <div class="content">
                        <p>Hola,</p>
                        <p>Te damos la bienvenida a ParkingIT4 Team. A continuación, encontrarás la información de inicio de sesión:</p>
                        <div class="info">
                            <p><strong>Correo:</strong> %s</p>
                            <p><strong>Contraseña:</strong> %s</p>
                        </div>
                        <p>Puedes iniciar sesión haciendo clic en el siguiente botón:</p>
                        <a href="http://portal-parqueo.s3-website.us-east-2.amazonaws.com/#/auth/login" class="button">Iniciar Sesión</a>
                    </div>
                    <div class="footer">
                        © 2024 ParkingIT4 Team. Todos los derechos reservados.
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