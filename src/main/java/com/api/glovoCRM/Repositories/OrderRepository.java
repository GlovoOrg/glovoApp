package com.api.glovoCRM.Repositories;

import com.api.glovoCRM.Models.OrderDetailModels.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
