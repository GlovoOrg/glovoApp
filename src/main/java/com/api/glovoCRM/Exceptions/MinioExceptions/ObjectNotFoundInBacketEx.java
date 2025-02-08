package com.api.glovoCRM.Exceptions.MinioExceptions;

public class ObjectNotFoundInBacketEx extends RuntimeException {
    public ObjectNotFoundInBacketEx(String message) {
        super(message);
    }
}
