package com.code.duel.code.duel.Exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super("Email '" + email + "' is already registered.");
    }

    public EmailAlreadyRegisteredException(String email, Throwable cause) {
        super("Email '" + email + "' is already registered.", cause);
    }
}

