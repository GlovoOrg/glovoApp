package com.api.glovoCRM.ExceptionHandlers;

import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler{

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DetailedError> handleException(Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        DetailedError errorDetail = DetailedError.builder()
                .timeStamp(new Date())
                .message(ex.getMessage())
                .method(request.getMethod())
                .path(request.getRequestURI())
                .errorCode("UNKNOWN_ERROR")
                .build();
        return new ResponseEntity<>(errorDetail, HttpStatus.valueOf(500));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(AlreadyExistsEx.class)
    public ResponseEntity<?> handleAlreadyExists(
            AlreadyExistsEx ex, HttpServletRequest request) {

        DetailedError errorDetail = DetailedError.builder()
                .timeStamp(new Date())
                .message(ex.getMessage())
                .method(request.getMethod())
                .path(request.getRequestURI())
                .errorCode("ALREADY_EXISTS")
                .build();
        return new ResponseEntity<>(errorDetail, HttpStatus.valueOf(409));
    }

    @ExceptionHandler(SuchResourceNotFoundEx .class)
    public ResponseEntity<?> handleSuchResourceNotFound(
            SuchResourceNotFoundEx ex, HttpServletRequest request) {

        DetailedError errorDetail = DetailedError.builder()
                .timeStamp(new Date())
                .message(ex.getMessage())
                .method(request.getMethod())
                .path(request.getRequestURI())
                .errorCode("NOT_FOUND")
                .build();
        return new ResponseEntity<>(errorDetail, HttpStatus.valueOf(404));
    }
}
