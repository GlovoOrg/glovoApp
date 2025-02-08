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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Transient
    private Long establishmentId;
    @Transient
    private Long discountProductId;
    @Transient
    private Long establishmentFilterId;

    @PrePersist
    private void prePersist(){
        if(this.establishment == null && this.establishmentId != null){
            Establishment establishment = new Establishment();
            establishment.setId(this.establishmentId);
            this.establishment = establishment;
        }
        if(this.discountProduct == null && this.discountProductId != null){
            DiscountProduct discountProduct = new DiscountProduct();
            discountProduct.setId(this.discountProductId);
            this.discountProduct = discountProduct;
        }
        if(this.establishmentFilter == null && this.establishmentFilterId != null){
            EstablishmentFilter establishmentFilter = new EstablishmentFilter();
            establishmentFilter.setId(this.establishmentFilterId);
            this.establishmentFilter = establishmentFilter;
        }
    }
}
