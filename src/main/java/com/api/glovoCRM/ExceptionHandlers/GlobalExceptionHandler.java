package com.api.glovoCRM.ExceptionHandlers;

import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<DetailedError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .filter(fieldError -> fieldError.getDefaultMessage() != null)
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage));


        DetailedError detailedError = DetailedError.builder()
                .timeStamp(new Date())
                .message("Ошибка валидации")
                .detail(errors.toString())
                .errorCode("VALIDATION_ERROR")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build();

        return ResponseEntity.badRequest().body(detailedError);
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
