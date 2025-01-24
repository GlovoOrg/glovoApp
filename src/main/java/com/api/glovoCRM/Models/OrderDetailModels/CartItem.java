package com.api.glovoCRM.Models.OrderDetailModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private int oneProductPriceCart;

    private int quantityOfProducts;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "orderDetail_id")
    private Order order;

}
