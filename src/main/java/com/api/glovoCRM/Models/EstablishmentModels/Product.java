package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.context.annotation.Lazy;

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

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @OneToOne(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DiscountProduct discountProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishment_id")
    @JsonBackReference
    private Establishment establishment;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishmentFilter_id")
    private EstablishmentFilter establishmentFilter;
}
