package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageDAO extends JpaRepository<Image, Long> {

}

