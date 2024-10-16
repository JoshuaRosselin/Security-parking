package org.grupouno.parking.it4.exceptions;

import org.springframework.dao.DataAccessException;

public class CustomDataAccessException extends DataAccessException {
    public CustomDataAccessException (String message, Throwable  e) {
        super(message, e);
    }
}
