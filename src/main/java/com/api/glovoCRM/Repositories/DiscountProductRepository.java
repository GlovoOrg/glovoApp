package com.api.glovoCRM.Repositories;

import com.api.glovoCRM.Models.EstablishmentModels.DiscountProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountProductRepository extends JpaRepository<DiscountProduct, Long> {
}
