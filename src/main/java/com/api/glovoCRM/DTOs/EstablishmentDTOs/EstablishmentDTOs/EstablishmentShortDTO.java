package com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentShortDTO extends BaseDTO {
    private int rating;
    private int quantityOfRatings;
    private double priceOfDelivery;
    private int timeOfDelivery;
    private boolean isOpen;
}
