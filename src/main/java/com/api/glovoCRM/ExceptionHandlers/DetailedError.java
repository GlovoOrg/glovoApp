package com.api.glovoCRM.ExceptionHandlers;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Builder
public record DetailedError(Date timeStamp, String message, String detail, String errorCode, String path, String method, HttpStatus status) {

}
