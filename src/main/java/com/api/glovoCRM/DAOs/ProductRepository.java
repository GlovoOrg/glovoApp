package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
