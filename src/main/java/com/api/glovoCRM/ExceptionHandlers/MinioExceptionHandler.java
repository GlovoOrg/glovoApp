package com.api.glovoCRM.ExceptionHandlers;

import com.api.glovoCRM.Exceptions.MinioExceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;




@RestControllerAdvice(basePackages = "com.api.glovoCRM.Controllers.Minio")
public class MinioExceptionHandler extends BaseExceptionHandler{

    @ExceptionHandler({
            InvalidFileTypeEx.class,
            FileValidationEx.class,
            FileUploadEx.class,
            FileDeleteEx.class,
            FileDownloadEx.class,
    })
    public ResponseEntity<DetailedError> handleFileExceptions(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "FILE_OPERATION_ERROR", null);
    }


}
