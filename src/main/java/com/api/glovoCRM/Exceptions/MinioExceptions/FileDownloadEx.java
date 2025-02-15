package com.api.glovoCRM.Exceptions.MinioExceptions;

public class FileDownloadEx extends RuntimeException {
    public FileDownloadEx(String message) {
        super(message);
    }
}
