package com.api.glovoCRM.Models.OrderDetailModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.api.glovoCRM.constants.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment_details")
public class PaymentDetail extends BaseEntity {
    //todo в будущем аннотации повесить, как решится вопрос с payment
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Заказ обязателен")
    private Order order;

    @Column(name = "session_id", nullable = false, unique = true)
    @NotNull(message = "Session ID обязателен")
    private String sessionId;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Статус оплаты обязателен")
    private PaymentStatus status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @NotNull(message = "Валюта обязательна")
    private String currency;

    @Column(name = "provider", nullable = false)
    @NotBlank(message = "Провайдер обязателен")
    private String provider;

    @Column(name = "payment_url", length = 1024)
    private String paymentUrl;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;

}
