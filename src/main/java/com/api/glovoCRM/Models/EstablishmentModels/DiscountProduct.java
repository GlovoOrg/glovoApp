package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "discounts")
@Getter
@Setter
public class DiscountProduct extends BaseEntity {
    @DecimalMin(value = "0", message = "Скидка не может быть меньше 0")
    @DecimalMax(value = "100", message = "Скидка не может быть больше 100")
    @Column(name = "discount", nullable = false)
    private int discount;

    @NotNull(message = "Статус активности обязателен")
    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Продукт обязателен")
    private Product product;

    @Transient
    private Long productId;

    @PrePersist
    private void prePersist() {
        if(this.product == null && this.productId != null) {
            Product p = new Product();
            p.setId(this.productId);
            this.product = p;
        }
    }
}
