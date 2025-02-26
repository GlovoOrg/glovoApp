package com.api.glovoCRM.DTOs.OrderDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    private Long id;
    private Long timeOfDelivery;
    private Long costOfDelivery;
    private double distance;
    private Long orderId;
}
