package com.api.glovoCRM.Rest.Requests.AuthRequests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshAccessTokenRequest {
    @NotNull(message = "refreshToken не может быть null")
    @NotEmpty(message = "refreshToken не может быть пустым")
    private String refreshToken;
}
