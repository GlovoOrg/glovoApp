package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentShortDTO extends BaseDTO{
    private Long subCategoryId;
    private Long establishmentAddressId;
    private String name;
    private double rating;
    private int quantityOfRatings;
    private double priceOfDelivery;
    private int timeOfDelivery;
    private boolean isOpen;
}
