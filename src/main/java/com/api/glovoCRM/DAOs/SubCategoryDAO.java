package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCategoryDAO extends JpaRepository<SubCategory, Long> {

}
