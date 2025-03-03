package com.api.glovoCRM.Repositories;

import com.api.glovoCRM.Models.EstablishmentModels.EstablishmentFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablishmentFilterRepository extends JpaRepository<EstablishmentFilter, Long>, JpaSpecificationExecutor<EstablishmentFilter> {

    boolean existsByNameAndEstablishmentId(String name, Long establishmentId);

}
