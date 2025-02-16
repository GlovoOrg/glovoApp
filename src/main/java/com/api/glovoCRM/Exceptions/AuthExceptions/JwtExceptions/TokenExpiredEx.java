package com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions;

public class TokenExpiredEx extends RuntimeException {
    public TokenExpiredEx(String message) {
        super(message);
    }

}
