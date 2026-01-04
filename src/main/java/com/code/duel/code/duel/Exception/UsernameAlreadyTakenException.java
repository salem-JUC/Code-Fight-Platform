package com.code.duel.code.duel.Exception;

public class UsernameAlreadyTakenException extends RuntimeException {
    public UsernameAlreadyTakenException(String username) {
        super("Username '" + username + "' is already taken.");
    }

    public UsernameAlreadyTakenException(String username, Throwable cause) {
        super("Username '" + username + "' is already taken.", cause);
    }
}

