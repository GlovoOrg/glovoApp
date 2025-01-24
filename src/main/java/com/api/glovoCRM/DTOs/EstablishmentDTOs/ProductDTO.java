package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal originalPrice;
    private BigDecimal finalPrice;
    private String image;
    private boolean active;
    private Double discountPercentage;
    private String discountMessage;
}
