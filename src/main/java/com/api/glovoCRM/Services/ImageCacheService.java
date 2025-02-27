package com.api.glovoCRM.Services;

import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.ImageAssociation;
import com.api.glovoCRM.constants.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ImageCacheService {

    private final ImageAssociationsDAO imageAssociationsDAO;
    @Autowired
    public ImageCacheService(ImageAssociationsDAO imageAssociationsDAO) {
        this.imageAssociationsDAO = imageAssociationsDAO;
    }

    @Cacheable (value = "imageAssociations", key = "#ownerId + ':' + #entityType.name()")
    public ImageAssociation getImageAssociation(Long ownerId, EntityType entityType) {
        return imageAssociationsDAO.findByOwnerIdAndEntityType(ownerId, entityType)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Ассоциация не найдена"));
    }

    @Cacheable(value = "imageUrls", key = "#imageUrl")
    public String extractObjectName(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("URL изображения не может быть пустым");
        }
        String[] parts = imageUrl.split("/");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Некорректный URL изображения");
        }
        return parts[parts.length - 1];
    }
}
