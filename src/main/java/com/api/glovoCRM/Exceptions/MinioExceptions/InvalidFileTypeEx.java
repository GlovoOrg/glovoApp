package com.api.glovoCRM.Exceptions.MinioExceptions;

public class InvalidFileTypeEx extends RuntimeException {
    public InvalidFileTypeEx(String message) {
        super(message);
    }
}
