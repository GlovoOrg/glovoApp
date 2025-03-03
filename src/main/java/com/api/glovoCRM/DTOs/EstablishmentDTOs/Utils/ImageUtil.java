package com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils;

import com.api.glovoCRM.Repositories.ImageAssociationsRepository;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.constants.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//чисто утилитарный класс для изображений
@Component
@Slf4j
public class ImageUtil {

    private static ImageAssociationsRepository imageAssociationsRepository;

    @Autowired
    public ImageUtil(ImageAssociationsRepository imageAssociationsRepository) {
        ImageUtil.imageAssociationsRepository = imageAssociationsRepository;
    }

    public static String getImageUrl(Long ownerId, EntityType entityType) {
        log.info("Получение imageUrl для ownerId: {} и entityType: {}", ownerId, entityType);
        return imageAssociationsRepository.findByOwnerIdAndEntityType(ownerId, entityType)
                .map(imageAssociation -> imageAssociation.getImage().getUrl())
                .orElseThrow(()-> new SuchResourceNotFoundEx("такого изображения нет для таких ownerId и ownerType"));
    }
}
