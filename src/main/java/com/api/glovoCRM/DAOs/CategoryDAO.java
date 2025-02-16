package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryDAO extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);
    @Query("select c from Category c LEFT JOIN FETCH c.subCategories")
    List<Category> findAllCategories();
}
