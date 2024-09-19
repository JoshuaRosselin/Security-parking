package org.grupouno.parking.it4.exceptions;

public class UserDeletionException extends RuntimeException {
    public UserDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}