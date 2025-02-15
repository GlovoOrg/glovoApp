package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO extends BaseDTO{
    private String description;
    private BigDecimal originalPrice;
    private BigDecimal finalPrice;
    private boolean active;
    private int discountPercentage;
    private String discountMessage;
    private DiscountProductDTO discountProductDTO;
}
