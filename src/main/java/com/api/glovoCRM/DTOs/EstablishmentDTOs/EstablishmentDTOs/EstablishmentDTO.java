package com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentDTO extends BaseDTO {
    private String name;
    private double priceOfDelivery;
    private int timeOfDelivery;
    private int rating;
    private int quantityOfRatings;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean isOpen;
    private List<ProductDTO> products;
}
