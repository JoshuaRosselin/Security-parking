package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.dto.VerificationCodeDto;
import org.grupouno.parking.it4.exceptions.InvalidVerificationCodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationCodeServiceTest {

    @Mock
    private AudithService audithService;

    @Mock
    private Logger logger;

    @InjectMocks
    private VerificationCodeService verificationCodeService;

    private Map<String, VerificationCodeDto> verificationCodes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        verificationCodes = new ConcurrentHashMap<>();
    }

    @Test
    void saveVerificationCode_success() {
        String email = "test@example.com";
        String code = "123456";
        verificationCodeService.saveVerificationCode(email, code);
        assertTrue(verificationCodeService.verificationCodes.containsKey(email));
        VerificationCodeDto savedCode = verificationCodeService.verificationCodes.get(email);
        assertEquals(code, savedCode.getCode());
        assertTrue(savedCode.getExpiry().isAfter(LocalDateTime.now()));

        verify(audithService, times(1)).createAudit(eq("VerificationCode"), contains("Saved verification code"), eq("CREATE"), anyMap(), isNull(), eq("Success"));
    }

    @Test
    void isVerificationCodeValid_success() throws InvalidVerificationCodeException {
        String email = "test@example.com";
        String code = "123456";
        VerificationCodeDto verificationCode = new VerificationCodeDto(code, LocalDateTime.now().plusMinutes(30));
        verificationCodeService.verificationCodes.put(email, verificationCode);
        boolean result = verificationCodeService.isVerificationCodeValid(email, code);
        assertTrue(result);
        verify(audithService, times(1)).createAudit(eq("VerificationCode"), contains("Successfully validated code"), eq("VALIDATE"), anyMap(), isNull(), eq("Success"));
    }

    @Test
    void isVerificationCodeValid_invalidEmail_throwsException() {
        String email = "test@example.com";
        String code = "123456";
        InvalidVerificationCodeException exception = assertThrows(InvalidVerificationCodeException.class, () ->
                verificationCodeService.isVerificationCodeValid(email, code));
        assertEquals("Email not Found.", exception.getMessage());
        verify(audithService, never()).createAudit(anyString(), anyString(), anyString(), anyMap(), anyMap(), anyString());
    }

    @Test
    void isVerificationCodeValid_incorrectCode_throwsException() {
        String email = "test@example.com";
        String code = "123456";
        VerificationCodeDto verificationCode = new VerificationCodeDto("654321", LocalDateTime.now().plusMinutes(30));
        verificationCodeService.verificationCodes.put(email, verificationCode);
        InvalidVerificationCodeException exception = assertThrows(InvalidVerificationCodeException.class, () ->
                verificationCodeService.isVerificationCodeValid(email, code));
        assertEquals("The code is incorrect.", exception.getMessage());
        verify(audithService, never()).createAudit(anyString(), anyString(), anyString(), anyMap(), anyMap(), anyString());
    }

    @Test
    void isVerificationCodeValid_expiredCode_throwsException() {
        String email = "test@example.com";
        String code = "123456";
        VerificationCodeDto verificationCode = new VerificationCodeDto(code, LocalDateTime.now().minusMinutes(1));
        verificationCodeService.verificationCodes.put(email, verificationCode);
        InvalidVerificationCodeException exception = assertThrows(InvalidVerificationCodeException.class, () ->
                verificationCodeService.isVerificationCodeValid(email, code));
        assertEquals("The code has expired.", exception.getMessage());
        verify(audithService, never()).createAudit(anyString(), anyString(), anyString(), anyMap(), anyMap(), anyString());
    }

    @Test
    @Scheduled(fixedRate = 60000)
    void cleanExpiredCodes_success() {
        String email = "test@example.com";
        VerificationCodeDto verificationCode = new VerificationCodeDto("123456", LocalDateTime.now().minusMinutes(1));
        verificationCodeService.verificationCodes.put(email, verificationCode);
        verificationCodeService.cleanExpiredCodes();
        assertFalse(verificationCodeService.verificationCodes.containsKey(email));
        verify(audithService, times(1)).createAudit(eq("VerificationCode"), contains("Cleaned up expired verification codes."), eq("CLEAN_UP"), isNull(), isNull(), eq("Success"));
    }

    @Test
    void auditAction_failure_logsError() {
        String entity = "VerificationCode";
        String description = "Test error";
        String operation = "CREATE";
        doThrow(new RuntimeException("Audit Service Failure")).when(audithService).createAudit(anyString(), anyString(), anyString(), anyMap(), anyMap(), anyString());
        verificationCodeService.saveVerificationCode("test@example.com", "123456");
        verify(logger, times(1)).error(contains("Error saving audit record"), anyString(), any(Throwable.class));
    }
}
