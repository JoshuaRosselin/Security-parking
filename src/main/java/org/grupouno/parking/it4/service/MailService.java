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
            helper.setSubject("Recuperación de Contraseña - ParkingIT4");

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
                    Recuperación de Contraseña
                </div>
                <div class="content">
                    <p>Hola,</p>
                    <p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en ParkingIT4 Team. Para continuar, utiliza el siguiente código de verificación:</p>
                    <div class="info">
                        <p><strong>Código de Verificación:</strong> %s</p>
                    </div>
                    <p>Ingresa este código en la página de recuperación de contraseña para establecer una nueva contraseña.</p>
                    <p>Si no solicitaste este cambio, por favor ignora este correo.</p>
                </div>
                <div class="footer">
                    © 2024 ParkingIT4 Team. Todos los derechos reservados.
                </div>
            </div>
        </body>
        </html>
        """;

            String formattedHtmlContent = String.format(htmlContent, code);
            helper.setText(formattedHtmlContent, true);
            mailSender.send(message);  // Send email
            logger.info("HTML email sent to {}, with verification code {}", email, code);

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