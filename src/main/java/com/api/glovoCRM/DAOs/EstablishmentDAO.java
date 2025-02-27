package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstablishmentDAO extends JpaRepository<Establishment, Long>, JpaSpecificationExecutor<Establishment> {

    boolean existsByName(@NotBlank(message = "Название не может быть null") @Size(min = 3, max = 355, message = "Название обязательно от 3 до 355 символов") String name);

    Optional<Establishment> findByName(String name);
    @Modifying
    @Query("delete from Establishment c where c.id=:id")
    void deleteEstablishmentById(@Param("id") Long id);
}
