package com.api.glovoCRM.Exceptions.AuthExceptions;

public class InvalidCredentialsEx extends RuntimeException{
    public InvalidCredentialsEx(String message){
        super(message);
    }
}
