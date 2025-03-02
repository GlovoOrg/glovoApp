package com.api.glovoCRM.DTOs.OrderDTOs;

import com.api.glovoCRM.constants.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailDTO {
    private Long id;
    private Long orderId;
    private String sessionId;
    private String transactionId;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private String provider;
    private String paymentUrl; // Ссылка на оплату
    private boolean isPaid;
}
