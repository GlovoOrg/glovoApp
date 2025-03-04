package com.api.glovoCRM.DTOs.UserDTOs;

import com.api.glovoCRM.constants.AuthProviders;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccountDTO {
    private AuthProviders provider;
    private String providerId;
}
