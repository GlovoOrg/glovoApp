package com.api.glovoCRM.Exceptions.AuthExceptions;


public class UserNotVerifiedEx extends RuntimeException{
    public UserNotVerifiedEx(String message){
        super(message);
    }
}
