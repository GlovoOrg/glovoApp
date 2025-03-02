package com.api.glovoCRM.DTOs.OrderDTOs;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class OrderItemDTO {
    private Long id;
    private int quantity;
    private BigDecimal oneProductPriceOrder;
    private Long orderId;
    private Long productId;
}
