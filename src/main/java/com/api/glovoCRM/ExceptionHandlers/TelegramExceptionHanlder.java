package com.api.glovoCRM.ExceptionHandlers;

import com.api.glovoCRM.Exceptions.Telegram.AccountNotBoundEx;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class TelegramExceptionHanlder extends BaseExceptionHandler{
    @ExceptionHandler(AccountNotBoundEx.class)
    public ResponseEntity<?> handleNotBoundException(HttpServletRequest req, AccountNotBoundEx ex) {
        return buildErrorResponse(ex, req, HttpStatus.BAD_REQUEST, "NOT_BOUNDED_ERROR", null);
    }
}
