package com.api.glovoCRM.Exceptions.MinioExceptions;

public class InvalidBucketEx extends RuntimeException {
    public InvalidBucketEx(String message) {
        super(message);
    }
}
