package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryDAO extends JpaRepository<SubCategory, Long>, JpaSpecificationExecutor<SubCategory> {
    boolean existsByName(String name);
    Optional<SubCategory> findByName(String name);
    Optional<SubCategory> findById(long id);
    @Query("select c from SubCategory c left join fetch c.establishments")
    List<SubCategory> findAllSubCategories();
}
