package com.api.glovoCRM.DTOs.OrderDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long id;
    private String addressLine;
    private double latitude;
    private double longitude;
    private Long orderId;
}
