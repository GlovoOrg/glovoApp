package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.EstablishmentFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablishmentFilterDAO extends JpaRepository<EstablishmentFilter, Long> {

}
