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

    @NotNull(message = "Скидка не может быть null, если скидки нет, то оставьте 0")
    @NotBlank(message = "Cкидка не может быть пустой")
    @PositiveOrZero(message = "Скидка не может быть отрицательной")
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
}
