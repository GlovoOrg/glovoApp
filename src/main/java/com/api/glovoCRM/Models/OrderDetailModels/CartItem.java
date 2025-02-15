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
@Table(name = "cart_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Продукт обязателен")
    private Product product;

    @Min(value = 0, message = "Количество не может быть меньше 0")
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Positive(message = "Цена одного продукта должна быть положительной")
    @Column(name = "one_product_price_cart", nullable = false)
    private BigDecimal oneProductPriceCart;

    @Positive(message = "Общая цена продуктов должна быть положительной")
    @Column(name = "total_price_cart", nullable = false)
    private BigDecimal totalPriceCart;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    @NotNull(message = "Корзина обязательна")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "orderDetail_id")
    @NotNull(message = "Заказ обязателен")
    private Order order;

    @PrePersist
    @PreUpdate
    public void setTotalPrice() {
        this.totalPriceCart = this.oneProductPriceCart.multiply(new BigDecimal(quantity));
    }
}
