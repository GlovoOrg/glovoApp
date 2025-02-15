package com.api.glovoCRM.Exceptions.MinioExceptions;

public class MinioConnectionEx extends RuntimeException {
    public MinioConnectionEx(String message) {
        super(message);
    }
}
