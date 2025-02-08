package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountProductDTO {
    private Long id;
    private int discount;
    private boolean active;
}
