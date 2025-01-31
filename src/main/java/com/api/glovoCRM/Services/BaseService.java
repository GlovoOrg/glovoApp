package com.api.glovoCRM.Services;

import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.Models.EstablishmentModels.Image;
import com.api.glovoCRM.Models.EstablishmentModels.ImageAssociation;
import com.api.glovoCRM.constants.EntityType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class BaseService<T, CreateRequest, UpdateRequest, PatchRequest> {
    protected final ImageDAO imageDAO;
    protected final ImageAssociationsDAO imageAssociationsDAO;
    protected final MinioService minioService;

    protected BaseService(ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService) {
        this.imageDAO = imageDAO;
        this.imageAssociationsDAO = imageAssociationsDAO;
        this.minioService = minioService;
    }

    public abstract T findById(Long id);
    public abstract List<T> findAll();
    public abstract T createEntity(CreateRequest request);
    public abstract void deleteEntity(Long entityId);
    public abstract T updateEntity(Long entityId, UpdateRequest request);
    public abstract T patchEntity(Long entityId, PatchRequest request);

    @Transactional
    protected Image createImageRecord(MultipartFile file, String bucketName, EntityType entityType, Long ownerId) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Файл не может быть пустым");
            }
            String objectId = generateObjectName(entityType, file);
            String imageUrl = minioService.uploadFile(file, bucketName, objectId);
            Image image = new Image();
            image.setUrl(imageUrl);
            image.setFilename(objectId);
            image.setSize(file.getSize());
            image.setContentType(file.getContentType());
            image.setBucket(bucketName);
            image.setOriginalFilename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unnamed_file");
            Image savedImage = minioService.saveImage(image);
            ImageAssociation association = new ImageAssociation();
            association.setImage(savedImage);
            association.setEntityType(entityType);
            association.setOwnerId(ownerId);
            imageAssociationsDAO.save(association);
            return savedImage;
        } catch (Exception e) {
            log.error("Ошибка создания Image: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при создании записи", e);
        }
    }

    @Transactional
    protected void deleteImageRecord(Long ownerId, EntityType entityType) {
        try {
            ImageAssociation imageAssociation = imageAssociationsDAO.findByOwnerIdAndEntityType(ownerId, entityType)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Ассоциация изображения не найдена"));
            Image image = imageAssociation.getImage();
            String objectName = extractObjectName(image.getUrl());
            log.debug("Удаление файла из MinIO: {}", objectName);
            if (imageAssociationsDAO.countByImage(image) > 1) {
                throw new RuntimeException("Изображение используется в других сущностях");
            }
            minioService.deleteFile(image.getBucket(), objectName);
            log.debug("Удаление записи из ImageAssociations...");
            imageAssociationsDAO.delete(imageAssociation);
        } catch (SuchResourceNotFoundEx e) {
            log.warn("Ошибка при удалении изображения: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении изображения: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить изображение", e);
        }
    }

    @Transactional
    protected void updateImageRecord(Long ownerId, EntityType entityType, MultipartFile newImage) {
        try {
            ImageAssociation imageAssociation = imageAssociationsDAO.findByOwnerIdAndEntityType(ownerId, entityType)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Ассоциация изображения не найдена"));
            Image image = imageAssociation.getImage();
            String oldObjectName = extractObjectName(image.getUrl());
            log.debug("Удаление старого файла из MinIO: {}", oldObjectName);
            minioService.deleteFile(image.getBucket(), oldObjectName);
            String newObjectId = generateObjectName(entityType, newImage);
            String newImageUrl = minioService.uploadFile(newImage, image.getBucket(), newObjectId);
            log.debug("Загрузка нового файла в MinIO. URL: {}", newImageUrl);
            image.setUrl(newImageUrl);
            image.setFilename(newObjectId);
            image.setSize(newImage.getSize());
            image.setOriginalFilename(newImage.getOriginalFilename());
            imageDAO.save(image);
        } catch (SuchResourceNotFoundEx e) {
            log.warn("Ошибка при обновлении изображения: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении изображения: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обновить изображение", e);
        }
    }

    // оригинальное расширение.
    protected String generateObjectName(EntityType entityType, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        return entityType.name().toLowerCase() + "-" + UUID.randomUUID() + extension;
    }

    protected String extractObjectName(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("URL изображения не может быть пустым");
        }
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}