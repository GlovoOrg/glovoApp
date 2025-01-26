package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.OrderDetailModels.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemDAO extends JpaRepository<OrderItem, Long> {
}
