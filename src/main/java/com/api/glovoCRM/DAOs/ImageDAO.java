package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageDAO extends JpaRepository<Image, Long> {
    boolean existsByBucketAndOriginalFilename(String bucket, String original_filename);
}

