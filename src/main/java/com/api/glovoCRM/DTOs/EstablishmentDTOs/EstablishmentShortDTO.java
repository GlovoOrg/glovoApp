package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentShortDTO extends BaseDTO{
    private String name;
    private double rating;
    private int quantityOfRatings;
    private double priceOfDelivery;
    private int timeOfDelivery;
    private boolean isOpen;
    private List<ProductDTO> products;
}
