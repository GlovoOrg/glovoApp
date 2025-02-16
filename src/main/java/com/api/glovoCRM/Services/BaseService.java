package com.api.glovoCRM.Services;

import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Rest.Requests.BaseRequest;
import com.api.glovoCRM.Rest.Requests.BaseRequestNotNull;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.Models.EstablishmentModels.Image;
import com.api.glovoCRM.Models.EstablishmentModels.ImageAssociation;
import com.api.glovoCRM.constants.EntityType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
@Component
@Slf4j
@Transactional
public abstract class BaseService<T, С extends BaseRequestNotNull, U extends BaseRequestNotNull, P extends BaseRequest> {

    public static final String CACHE_PREFIX = "entity_";

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
    public abstract T createEntity(С request);
    public abstract void deleteEntity(Long entityId);
    public abstract T updateEntity(Long entityId, U request);
    public abstract T patchEntity(Long entityId, P request);
    public abstract List<T> findSimilarByNameFilter(String name);

    protected Image createImageRecord(MultipartFile file, String bucketName, EntityType entityType, Long ownerId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }
        String imageUrl = null;
        String objectName = generateObjectName(entityType, file);
        try {
            imageUrl = minioService.uploadFile(file, bucketName, objectName);


            Image image = Image.builder()
                    .url(imageUrl)
                    .filename(objectName)
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .bucket(bucketName)
                    .originalFilename(file.getOriginalFilename())
                    .build();

            Image savedImage = imageDAO.save(image);

            ImageAssociation association = new ImageAssociation();
            association.setImage(savedImage);
            association.setEntityType(entityType);
            association.setOwnerId(ownerId);
            imageAssociationsDAO.save(association);
            return savedImage;
        } catch (Exception e) {
            log.error("Ошибка создания Image: {}", e.getMessage(), e);
            if (imageUrl != null) {
                minioService.deleteFile(bucketName, objectName);
            }
            throw new RuntimeException("Ошибка при создании записи", e);
        }
    }


    protected void deleteImageRecord(Long ownerId, EntityType entityType) {
        try {
            ImageAssociation imageAssociation = imageAssociationsDAO.findByOwnerIdAndEntityType(ownerId, entityType)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Ассоциация изображения не найдена"));

            Image image = imageAssociation.getImage();

            imageAssociationsDAO.delete(imageAssociation);
            log.debug("Удаление записи из ImageAssociations...");


            String objectName = extractObjectName(image.getUrl());

            if (!minioService.validateObjectInBucket(image.getBucket(), objectName)) {
                log.warn("Объект {} уже удален из MinIO", objectName);
            } else {
                minioService.deleteFile(image.getBucket(), objectName);
            }

        } catch (SuchResourceNotFoundEx e) {
            log.warn("Ошибка при удалении изображения: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении изображения: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить изображение", e);
        }
    }

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
//    private String getBucketForEntityType(EntityType entityType) {
//        return switch (entityType) {
//            case Category -> "categories";
//            case Product -> "products";
//            case Establishment -> "establishments";
//            default -> throw new IllegalArgumentException("Неизвестный тип");
//        };
//    }
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
        String[] parts = imageUrl.split("/");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Некорректный URL изображения");
        }
        return parts[parts.length - 1];
    }
}