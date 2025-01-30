package com.api.glovoCRM.ExceptionHandlers;

import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice()
public class GlobalExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DetailedError> handleException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_ERROR", null);
    }

    @ExceptionHandler(AlreadyExistsEx.class)
    public ResponseEntity<?> handleAlreadyExists(AlreadyExistsEx ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT, "ALREADY_EXISTS", null);
    }

    @ExceptionHandler(SuchResourceNotFoundEx.class)
    public ResponseEntity<?> handleSuchResourceNotFound(SuchResourceNotFoundEx ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "NOT_FOUND", null);
    }
}
