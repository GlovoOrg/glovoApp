package com.api.glovoCRM.Models.TelegramBotModels;


import lombok.Data;

@Data
public class ErrorDetail {

    private String message;

    private int code;
}
