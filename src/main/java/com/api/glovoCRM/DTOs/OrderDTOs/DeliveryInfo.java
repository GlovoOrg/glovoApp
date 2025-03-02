package com.api.glovoCRM.DTOs.OrderDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryInfo {
    private double distanceKm;
    private double durationMin;
    private BigDecimal cost;
}
