package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.DiscountProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountProductDAO extends JpaRepository<DiscountProduct, Long> {
}
