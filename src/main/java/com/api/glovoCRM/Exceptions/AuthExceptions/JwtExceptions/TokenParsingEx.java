package com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions;

public class TokenParsingEx extends RuntimeException {
    public TokenParsingEx(String message) {
        super(message);
    }
}
