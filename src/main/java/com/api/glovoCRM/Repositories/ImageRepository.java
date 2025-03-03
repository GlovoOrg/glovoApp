package com.api.glovoCRM.Repositories;

import com.api.glovoCRM.Models.EstablishmentModels.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByUrl(String oldObjectUrl);
}

