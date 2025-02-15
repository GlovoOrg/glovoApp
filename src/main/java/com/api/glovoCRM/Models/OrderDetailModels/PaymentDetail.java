package com.api.glovoCRM.Models.OrderDetailModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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


    @NotBlank(message = "Провайдер обязателен")
    @Column(name = "provider",  nullable = false)
    private String provider;

    @NotNull(message = "Статус оплаты обязателен")
    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;
}
