package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablishmentDAO extends JpaRepository<Establishment, Long> {

}
