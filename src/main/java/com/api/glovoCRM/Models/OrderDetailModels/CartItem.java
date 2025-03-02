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
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "CartItem", timeToLive = 86400)
public class CartItem {
    @Id
    private String id;

    @Indexed
    private Long productId;

    private int quantity;

    private BigDecimal oneProductPriceCart;

    private BigDecimal totalPriceCart;

    @Indexed
    private String cartId;

    public void recalculateTotal() {
        this.totalPriceCart = oneProductPriceCart.multiply(BigDecimal.valueOf(quantity));
    }
}

