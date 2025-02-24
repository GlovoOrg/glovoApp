package com.api.glovoCRM.Models.OrderDetailModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.api.glovoCRM.Models.UserModels.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//redisHash

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Cart", timeToLive = 86400)
public class Cart {
    @Id
    private String id;

    @Indexed
    private Long userId;

    private BigDecimal totalCharge = BigDecimal.ZERO;

    private List<CartItem> items = new ArrayList<>();

    public void recalculateTotal() {
        this.totalCharge = items.stream()
                .map(CartItem::getTotalPriceCart)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

