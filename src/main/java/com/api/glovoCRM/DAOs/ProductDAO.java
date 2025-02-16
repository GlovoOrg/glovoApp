package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductDAO extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByName(String name);


}
