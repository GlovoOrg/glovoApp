package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDAO extends JpaRepository<Category, Long> {
}
