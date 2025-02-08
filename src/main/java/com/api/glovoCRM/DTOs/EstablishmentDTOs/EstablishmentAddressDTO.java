package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentAddressDTO {
    private Long id;
    private String addressLine;
    private double latitude;
    private double longitude;
}
