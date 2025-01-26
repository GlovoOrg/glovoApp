package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.OrderDetailModels.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailDAO extends JpaRepository<OrderDetail, Long> {

}
