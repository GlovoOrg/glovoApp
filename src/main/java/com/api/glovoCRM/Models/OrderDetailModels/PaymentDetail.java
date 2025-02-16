package com.api.glovoCRM.Models.OrderDetailModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment_details")
public class PaymentDetail extends BaseEntity {
    //todo в будущем аннотации повесить, как решится вопрос с payment
    @OneToOne
    @JoinColumn(name = "orderDetail_id", nullable = false)
    @NotNull(message = "Заказ обязателен")
    private Order order;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @NotNull(message = "Статус оплаты обязателен")
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @NotBlank(message = "Провайдер обязателен")
    @Column(name = "provider",  nullable = false)
    private String provider;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;
}
