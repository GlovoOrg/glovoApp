package com.api.glovoCRM.Models.OrderDetailModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail extends BaseEntity {

    @Positive(message = "Время доставки должно быть положительным")
    @Column(name = "time_of_delivery", nullable = false)
    private int timeOfDelivery;

    @PositiveOrZero(message = "Стоимость доставки не может быть отрицательной")
    @Column(name = "cost_of_delivery", nullable = false)
    private int costOfDelivery;

    @PositiveOrZero(message = "Расстояние не может быть отрицательным")
    @Column(name = "distance", nullable = false)
    private double distance;

    @OneToOne
    @JoinColumn(name = "order_id")
    @NotNull(message = "Заказ обязателен")
    private Order order;

}
