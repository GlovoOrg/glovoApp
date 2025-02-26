package com.api.glovoCRM.DTOs.OrderDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailDTO {
    private String id;
    private Long orderId;
}
