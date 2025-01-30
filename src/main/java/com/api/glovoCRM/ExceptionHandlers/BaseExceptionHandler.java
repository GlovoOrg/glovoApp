package com.api.glovoCRM.ExceptionHandlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@Slf4j
public abstract class BaseExceptionHandler {

    protected ResponseEntity<DetailedError> buildErrorResponse(
            Exception ex,
            HttpServletRequest request,
            HttpStatus status,
            String errorCode,
            String details
    ) {
        log.error("Ошибка произошла: {}, Метод: {}, Путь: {}",
                ex.getMessage(),
                request.getMethod(),
                request.getRequestURI()
        );

        DetailedError errorDetail = DetailedError.builder()
                .timeStamp(new Date())
                .message(ex.getMessage())
                .method(request.getMethod())
                .path(request.getRequestURI())
                .detail(details)
                .errorCode(errorCode)
                .status(status)
                .build();

        return new ResponseEntity<>(errorDetail, status);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<DetailedError> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "INVALID_INPUT", "Некорректный MIME-тип файла");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DetailedError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "INVALID_INPUT", "Ошибка валидации запроса");
    }
}

