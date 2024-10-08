package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.dto.VerificationCodeDto;
import org.grupouno.parking.it4.exceptions.InvalidVerificationCodeException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeService {
    private final Map<String, VerificationCodeDto> verificationCodes = new ConcurrentHashMap<>();

    public void saveVerificationCode(String email, String code) {
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
        VerificationCodeDto verificationCode = new VerificationCodeDto(code, expiry);
        verificationCodes.put(email, verificationCode);

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
        return true;
    }

    @Scheduled(fixedRate = 60000)
    public void cleanExpiredCodes() {
        verificationCodes.entrySet().removeIf(entry -> entry.getValue().getExpiry().isBefore(LocalDateTime.now()));
    }
}
