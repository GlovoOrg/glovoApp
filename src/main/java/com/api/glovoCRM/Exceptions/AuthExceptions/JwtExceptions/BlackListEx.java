package com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BlackListEx extends RuntimeException{

    public BlackListEx(String message){
        super(message);
    }
}
