package com.api.glovoCRM.Repositories;

import com.api.glovoCRM.Models.OrderDetailModels.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

}
