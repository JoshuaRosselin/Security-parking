package org.grupouno.parking.it4.exceptions;

public class CustomIllegalArgumentException extends  IllegalArgumentException{
    public CustomIllegalArgumentException (String message, Throwable  e) {
        super(message, e);
    }
}
