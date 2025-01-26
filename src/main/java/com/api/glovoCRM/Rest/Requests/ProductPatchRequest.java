package com.api.glovoCRM.Rest.Requests;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
@Data
public class ProductPatchRequest {
    @Size(min = 3, max = 266, message = "Название должно быть от 3 до 266 символов")
    private String name;
    @Size(max = 1000, message = "Описание не может превышать 1000 символов")
    private String description;
    @Positive(message = "Цена должна быть положительной")
    private BigDecimal price;

    private MultipartFile image;
    private Boolean active;

    private DiscountProductRequest discount;
}
