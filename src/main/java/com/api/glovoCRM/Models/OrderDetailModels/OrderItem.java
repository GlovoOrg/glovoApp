package com.api.glovoCRM.Models.OrderDetailModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    private int quantity = 0;

    private double OneProductPriceOrder;

    @ManyToOne
    @JoinColumn(name = "orderDetail_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
