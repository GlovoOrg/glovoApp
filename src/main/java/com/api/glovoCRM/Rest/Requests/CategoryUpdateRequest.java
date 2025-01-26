package com.api.glovoCRM.Rest.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CategoryUpdateRequest {
    @NotBlank
    private String name;
    @NotNull
    private MultipartFile image;
}
