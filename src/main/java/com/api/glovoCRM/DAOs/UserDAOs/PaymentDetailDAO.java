package com.api.glovoCRM.DAOs.UserDAOs;

import com.api.glovoCRM.Models.OrderDetailModels.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailDAO extends JpaRepository<PaymentDetail, Long> {
    PaymentDetail findBySessionId(String sessionId);
}
