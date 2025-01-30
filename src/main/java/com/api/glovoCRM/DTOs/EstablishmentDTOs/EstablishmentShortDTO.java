package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import com.api.glovoCRM.Models.EstablishmentModels.ImageAssociation;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentShortDTO {
    private Long id;
    private String name;
    private int rating;
    private int quantityOfRatings;
    private double priceOfDelivery;
    private int timeOfDelivery;
    private boolean isOpen;
}
