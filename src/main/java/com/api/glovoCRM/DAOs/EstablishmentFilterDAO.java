package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.EstablishmentFilter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablishmentFilterDAO extends JpaRepository<EstablishmentFilter, Long>, JpaSpecificationExecutor<EstablishmentFilter> {

    boolean existsByNameAndEstablishmentId(String name, Long establishmentId);

}
