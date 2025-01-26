package com.api.glovoCRM.Rest.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
@Data
public class ProductUpdateRequest {
    @NotBlank
    private String name;
    @NotBlank private String description;
    @Positive
    private BigDecimal price;
    private MultipartFile image;
    private boolean active;
    private DiscountProductRequest discount;
}
