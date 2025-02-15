package com.api.glovoCRM.Rest.Responses.Auth;

import lombok.Builder;

@Builder
public record RegisterResponse(
        String message
) {
}
