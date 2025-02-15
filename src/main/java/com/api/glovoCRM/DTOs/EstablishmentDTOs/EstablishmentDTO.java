package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentDTO extends BaseDTO{
    private double priceOfDelivery;
    private int timeOfDelivery;
    private double rating;
    private int quantityOfRatings;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean isOpen;
    private List<ProductDTO> products;
    private List<EstablishmentFilterDTO> establishmentFilters;
    private EstablishmentAddressDTO establishmentAddressDTO;
}
