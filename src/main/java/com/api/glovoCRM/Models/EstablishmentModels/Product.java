package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Table(name = "products")
@Entity
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "image_url")
    private String image;
    //todo в будущем надо реализовать эндпоинт чтобы можно было менять значение с ролью Establishment
    private boolean active = true;

    @OneToOne(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DiscountProduct discountProduct;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "establishment_id")
    private Establishment establishment;

    @ManyToMany
    @JoinTable(
            name = "product_establishment_filter",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "establishment_filter_id")
    )
    private List<EstablishmentFilter> establishmentFilters;
}
