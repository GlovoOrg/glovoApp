package com.api.glovoCRM.Rest.Responses;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record FileUploadResponse(
        String url,
        String filename,
        String bucket,
        LocalDateTime uploadedAt
) {}
