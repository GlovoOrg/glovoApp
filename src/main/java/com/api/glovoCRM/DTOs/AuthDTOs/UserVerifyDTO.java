package com.api.glovoCRM.DTOs.AuthDTOs;

import lombok.Data;

@Data
public class UserVerifyDTO {
    private String token;  // Для подтверждения по ссылке
    private String email;  // Для повторной отправки
    private String code;   // Для ручного ввода кода
}
