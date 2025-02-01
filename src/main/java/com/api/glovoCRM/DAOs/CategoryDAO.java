package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryDAO extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);
    Optional<Category> getCategoryById(Long categoryId);
}
