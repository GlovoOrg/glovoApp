package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.OrderDetailModels.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDAO extends JpaRepository<Order, Long> {

}
