package com.api.glovoCRM.Exceptions.MinioExceptions;

public class BucketCreationEx extends RuntimeException{
    public BucketCreationEx(String message){
        super(message);
    }
}
