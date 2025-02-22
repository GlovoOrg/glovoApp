package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "Категория обязательна") Optional<Category> getCategoryById(@NotNull(message = "ID категории обязательно") @Positive(message = "Id категории должно быть положительным") Long categoryId);
}
