package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode (callSuper = true)
@Data
public class ProductForEstablishmentDTO extends BaseDTO{
    private String description;
    private BigDecimal originalPrice;
    private BigDecimal finalPrice;
    private boolean active;
    private int discountPercentage;
    private String discountMessage;
    private DiscountProductDTO discountProduct;
}
