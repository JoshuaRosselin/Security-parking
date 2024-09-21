package org.grupouno.parking.it4.exceptions;

public class InvalidVerificationCodeException extends RuntimeException {

    public InvalidVerificationCodeException(String message) {
        super(message);
    }

    public InvalidVerificationCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
