package com.api.glovoCRM.Models.OrderDetailModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @Min(value = 0, message = "Количество не может быть меньше 0")
    @Column(name = "quantity", nullable = false)
    private int quantity = 0;

    @Positive(message = "Цена должна быть положительной")
    @Column(name = "one_product_price_order", nullable = false)
    private BigDecimal OneProductPriceOrder;

    @ManyToOne
    @JoinColumn(name = "order_detail_id", nullable = false)
    @NotNull(message = "Заказ обязателен")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Продукт обязателен")
    private Product product;

}
