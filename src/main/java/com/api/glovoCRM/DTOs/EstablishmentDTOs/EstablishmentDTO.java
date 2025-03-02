package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstablishmentDTO extends BaseDTO{
    private Long subcategoryId;
    private double priceOfDelivery;
    private int timeOfDelivery;
    private double rating;
    private int quantityOfRatings;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean openNow;
    private List<ProductDTO> products;
    private List<EstablishmentFilterDTO> establishmentFilters;
    private EstablishmentAddressDTO establishmentAddress;
}
