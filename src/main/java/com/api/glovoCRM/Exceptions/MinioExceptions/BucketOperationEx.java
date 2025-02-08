package com.api.glovoCRM.Exceptions.MinioExceptions;

public class BucketOperationEx extends RuntimeException {
    public BucketOperationEx(String message) {
        super(message);
    }
}
