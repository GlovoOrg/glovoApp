package com.api.glovoCRM.Models.OrderDetailModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail extends BaseEntity {

    private int timeOfDelivery;

    private int costOfDelivery;

    private double distance;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
