package com.api.glovoCRM.ExceptionHandlers;

import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.BlackListEx;
import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.InvalidTokenEx;
import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.TokenExpiredEx;
import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.TokenParsingEx;
import com.api.glovoCRM.Exceptions.AuthExceptions.UserNotVerifiedEx;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice()
public class JwtExceptionHandler extends BaseExceptionHandler{
    @ExceptionHandler(TokenExpiredEx.class)
    public ResponseEntity<?> handleTokenExpiredException(TokenExpiredEx ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "Срок действия токена истек.");
    }

    @ExceptionHandler(InvalidTokenEx.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenEx ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "Невалидный токен");
    }

    @ExceptionHandler(TokenParsingEx.class)
    public ResponseEntity<?> handleTokenParsingException(TokenParsingEx ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_PARSING_ERROR", "Ошибка при парсинге токена");
    }
    @ExceptionHandler(BlackListEx.class)
    public ResponseEntity<?> handleBlackListException(BlackListEx ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.FORBIDDEN, "TOKENT_BLACKLISTED_ERROR", "Ошибка с blacklisting");
    }
    @ExceptionHandler(UserNotVerifiedEx.class)
    public ResponseEntity<?> handleUserNotVerifiedException(UserNotVerifiedEx ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.FORBIDDEN, "USER_NOT_VERIFIED", "Подтверди почту или номер");
    }
}
