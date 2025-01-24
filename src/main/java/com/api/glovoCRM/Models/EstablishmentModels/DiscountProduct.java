package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "discounts")
@Getter
@Setter
public class DiscountProduct extends BaseEntity {

    private double discount;

    private boolean active;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
