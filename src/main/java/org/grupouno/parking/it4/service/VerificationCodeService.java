package org.grupouno.parking.it4.service;

import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.VerificationCodeDto;
import org.grupouno.parking.it4.exceptions.InvalidVerificationCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Service
public class VerificationCodeService {
    private static final Logger logger = LoggerFactory.getLogger(VerificationCodeService.class);
    public final Map<String, VerificationCodeDto> verificationCodes = new ConcurrentHashMap<>();
    private static final String VERIFICATIONCODE ="VerificationCode";
    private static final String SUCCESS = "Success";

    private final AudithService audithService;

    public void saveVerificationCode(String email, String code) {
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
        VerificationCodeDto verificationCode = new VerificationCodeDto(code, expiry);
        verificationCodes.put(email, verificationCode);
        auditAction(VERIFICATIONCODE, "Saved verification code for email: " + email, "CREATE",
                Map.of("email", email, "code", code), null, SUCCESS);
    }

    public boolean isVerificationCodeValid(String email, String code) throws InvalidVerificationCodeException {
        VerificationCodeDto verificationCode = verificationCodes.get(email);
        if (verificationCode == null) {
            throw new InvalidVerificationCodeException("Email not Found.");
        }
        if (!verificationCode.getCode().equals(code)) {
            throw new InvalidVerificationCodeException("The code is incorrect.");
        }
        if (verificationCode.getExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidVerificationCodeException("The code has expired.");
        }

        auditAction(VERIFICATIONCODE, "Successfully validated code for email: " + email, "VALIDATE",
                Map.of("email", email, "code", code), null, SUCCESS);
        return true;
    }

    @Scheduled(fixedRate = 60000)
    public void cleanExpiredCodes() {
        verificationCodes.entrySet().removeIf(entry -> entry.getValue().getExpiry().isBefore(LocalDateTime.now()));

        auditAction(VERIFICATIONCODE, "Cleaned up expired verification codes.", "CLEAN_UP",
                null, null, SUCCESS);
    }

    private void auditAction(String entity, String description, String operation,
                             Map<String, Object> request, Map<String, Object> response, String result) {
        try {
            audithService.createAudit(entity, description, operation, request, response, result);
        } catch (Exception e) {
            logger.error("Error saving audit record: {}", e.getMessage(), e);
        }
    }
}
