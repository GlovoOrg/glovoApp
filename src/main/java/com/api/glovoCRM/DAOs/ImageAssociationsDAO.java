package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Image;
import com.api.glovoCRM.Models.EstablishmentModels.ImageAssociation;
import com.api.glovoCRM.constants.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageAssociationsDAO extends JpaRepository<ImageAssociation, Long> {
    Optional<ImageAssociation> findByOwnerIdAndEntityType(Long ownerId, EntityType entityType);

    int countByImage(Image image);
}
