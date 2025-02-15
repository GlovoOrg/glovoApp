package com.api.glovoCRM.Rest.Responses.Auth;

import lombok.Builder;

@Builder
public record LoginResponse(
       String accessToken,
       String refreshToken
) {
}
