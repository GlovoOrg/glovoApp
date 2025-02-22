package com.api.glovoCRM.DAOs;

import com.api.glovoCRM.Models.EstablishmentModels.Image;
import com.api.glovoCRM.Models.EstablishmentModels.ImageAssociation;
import com.api.glovoCRM.constants.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ImageAssociationsDAO extends JpaRepository<ImageAssociation, Long> {
    Optional<ImageAssociation> findByOwnerIdAndEntityType(Long ownerId, EntityType entityType);
    @Modifying
    @Transactional
    @Query("DELETE FROM ImageAssociation ia WHERE ia.ownerId = :ownerId AND ia.entityType = :entityType")
    void deleteAllByOwnerIdAndEntityType(@Param("ownerId") Long ownerId, @Param("entityType") EntityType entityType);


    int countByImage(Image image);
}
