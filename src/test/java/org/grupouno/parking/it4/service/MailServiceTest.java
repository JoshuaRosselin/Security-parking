package org.grupouno.parking.it4.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendVerificationCode_success() throws Exception {
        String email = "test@example.com";
        String code = "123456";
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.sendVerificationCode(email, code);

        verify(mailSender, times(1)).send(mimeMessage);
    }



    @Test
    void sendPasswordAndUser_success() throws Exception {
        String email = "test@example.com";
        String password = "password123";
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.sendPasswordAndUser(email, password);

        verify(mailSender, times(1)).send(mimeMessage);
    }


}
