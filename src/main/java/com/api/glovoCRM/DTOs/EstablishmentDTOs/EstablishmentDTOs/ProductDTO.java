package com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO extends BaseDTO {
    private String name;
    private String description;
    private BigDecimal originalPrice;
    private BigDecimal finalPrice;
    private boolean active;
    private Double discountPercentage;
    private String discountMessage;
}
