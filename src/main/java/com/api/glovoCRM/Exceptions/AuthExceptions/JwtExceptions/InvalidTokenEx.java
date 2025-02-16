package com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions;

public class InvalidTokenEx extends RuntimeException {
    public InvalidTokenEx(String message) {
        super(message);
    }
}
