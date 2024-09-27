package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.dto.VerificationCodeDto;
import org.grupouno.parking.it4.exceptions.InvalidVerificationCodeException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

@Service
public class VerificationCodeService {
    private final Map<String, VerificationCodeDto> verificationCodes = new ConcurrentHashMap<>();
    private final AudithService audithService; // Inyección del servicio de auditoría

    public VerificationCodeService(AudithService audithService) {
        this.audithService = audithService;
    }

    public void saveVerificationCode(String email, String code) {
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
        VerificationCodeDto verificationCode = new VerificationCodeDto(code, expiry);
        verificationCodes.put(email, verificationCode);

        // Registrar auditoría para la creación del código de verificación
        auditAction("VerificationCode", "Saved verification code for email: " + email, "CREATE",
                Map.of("email", email, "code", code), null, "Success");
    }

    public boolean isVerificationCodeValid(String email, String code) throws InvalidVerificationCodeException {
        VerificationCodeDto verificationCode = verificationCodes.get(email);
        if (verificationCode == null) {
            auditAction("VerificationCode", "Failed to validate code - Email not found: " + email, "VALIDATE",
                    Map.of("email", email, "code", code), null, "Failure");
            throw new InvalidVerificationCodeException("Email not Found.");
        }
        if (!verificationCode.getCode().equals(code)) {
            auditAction("VerificationCode", "Failed to validate code - Incorrect code for email: " + email, "VALIDATE",
                    Map.of("email", email, "code", code), null, "Failure");
            throw new InvalidVerificationCodeException("The code is incorrect.");
        }
        if (verificationCode.getExpiry().isBefore(LocalDateTime.now())) {
            auditAction("VerificationCode", "Failed to validate code - Code expired for email: " + email, "VALIDATE",
                    Map.of("email", email, "code", code), null, "Failure");
            throw new InvalidVerificationCodeException("The code has expired.");
        }

        // Registrar auditoría para la validación exitosa del código
        auditAction("VerificationCode", "Successfully validated code for email: " + email, "VALIDATE",
                Map.of("email", email, "code", code), null, "Success");
        return true;
    }

    @Scheduled(fixedRate = 60000)
    public void cleanExpiredCodes() {
        verificationCodes.entrySet().removeIf(entry -> entry.getValue().getExpiry().isBefore(LocalDateTime.now()));

        // Registrar auditoría para la limpieza de códigos expirados
        auditAction("VerificationCode", "Cleaned up expired verification codes.", "CLEAN_UP",
                null, null, "Success");
    }

    private void auditAction(String entity, String description, String operation,
                             Map<String, Object> request, Map<String, Object> response, String result) {
        try {
            audithService.createAudit(entity, description, operation, request, response, result);
        } catch (Exception e) {
            // Manejo de errores, puedes usar un logger aquí
            System.err.println("Error saving audit record: " + e.getMessage());
        }
    }
}
