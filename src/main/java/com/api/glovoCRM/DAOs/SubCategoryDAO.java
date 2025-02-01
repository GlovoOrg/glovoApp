package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubCategoryDAO extends JpaRepository<SubCategory, Long> {
    boolean existsByName(String name);
    Optional<SubCategory> findByName(String name);
    Optional<SubCategory> findById(long id);
}
