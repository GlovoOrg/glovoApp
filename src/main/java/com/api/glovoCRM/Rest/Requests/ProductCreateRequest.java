package com.api.glovoCRM.Rest.Requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class ProductCreateRequest {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long establishmentId; // Только ID заведения
    private Long establishmentFilterId;
    private MultipartFile image;
    private DiscountProductRequest discountProduct;
}
