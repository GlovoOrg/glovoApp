package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Table(name = "products")
@Entity
public class Product extends BaseEntity {

    @NotBlank(message = "Название продукта обязательно")
    @Size(max = 266, message = "Максимальная длина названия — 266 символов")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Описание продукта обязательно не может быть null")
    @NotBlank(message = "Описание продукта обязательно")
    @Size(max = 1000, message = "Максимальная длина описания — 1000 символов")
    @Column(name = "description", nullable = false)
    private String description;

    @Positive(message = "Цена должна быть положительной")
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    //todo в будущем надо реализовать эндпоинт чтобы можно было менять значение с ролью Establishment
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @OneToOne(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DiscountProduct discountProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishment_id")
    private Establishment establishment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishmentFilter_id")
    private EstablishmentFilter establishmentFilter;
}
