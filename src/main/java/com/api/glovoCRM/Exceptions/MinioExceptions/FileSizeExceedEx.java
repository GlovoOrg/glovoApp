package com.api.glovoCRM.Exceptions.MinioExceptions;

public class FileSizeExceedEx extends RuntimeException {
    public FileSizeExceedEx(String message) {
        super(message);
    }
}
