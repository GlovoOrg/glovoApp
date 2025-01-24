package com.api.glovoCRM.Exceptions.BaseExceptions;

public class AlreadyExistsEx extends RuntimeException {
    public AlreadyExistsEx(String message) {
        super(message);
    }
}
