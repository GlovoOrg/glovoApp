package com.api.glovoCRM.Repositories.UserRepositories;

import com.api.glovoCRM.Models.OrderDetailModels.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Long> {
    PaymentDetail findBySessionId(String sessionId);
}
