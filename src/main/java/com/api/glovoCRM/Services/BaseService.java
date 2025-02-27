package com.api.glovoCRM.Services;

import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.*;
import com.api.glovoCRM.Rest.Requests.BaseRequest;
import com.api.glovoCRM.Rest.Requests.BaseRequestNotNull;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.Models.EstablishmentModels.Image;
import com.api.glovoCRM.Models.EstablishmentModels.ImageAssociation;
import com.api.glovoCRM.constants.EntityType;
import io.minio.errors.MinioException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@Transactional
public abstract class BaseService<T, C extends BaseRequestNotNull, U extends BaseRequestNotNull, P extends BaseRequest> {

    protected final ImageDAO imageDAO;
    protected final ImageAssociationsDAO imageAssociationsDAO;
    protected final MinioService minioService;
    protected final ImageCacheService imageCacheService;

    @Autowired
    protected BaseService(ImageCacheService imageCacheService, ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService) {
        this.imageDAO = imageDAO;
        this.imageAssociationsDAO = imageAssociationsDAO;
        this.minioService = minioService;
        this.imageCacheService = imageCacheService;
    }

    public abstract T findById(Long id);

    public abstract List<T> findAll();

    public abstract T createEntity(C request);

    public abstract void deleteEntity(Long entityId);

    public abstract T updateEntity(Long entityId, U request) throws MinioException;

    public abstract T patchEntity(Long entityId, P request) throws MinioException;

    public abstract List<T> findSimilarByNameFilter(String name);

    protected void createImageRecord(MultipartFile file, String bucketName, EntityType entityType, Long ownerId) throws MinioException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }
        String imageUrl;
        String objectName = generateObjectName(entityType, file);
        try {
            imageUrl = minioService.uploadFile(file, bucketName, objectName);
        } catch (FileUploadEx | MinioException | MinioOperationEx e) {
            log.error("Ошибка создания Image: {}", e.getMessage(), e);
            throw new FileUploadEx("Ошибка сохранения изображения");
        }
        if (imageUrl != null) {
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
        }
    }

    @CacheEvict (value = {"imageAssociations", "imageUrls"}, allEntries = true)
    public void deleteImageRecord(Long ownerId, EntityType entityType) throws MinioException, FileDeleteEx, SuchResourceNotFoundEx {

        ImageAssociation imageAssociation = imageCacheService.getImageAssociation(ownerId, entityType);
        Image image = imageAssociation.getImage();
        String objectName = imageCacheService.extractObjectName(image.getUrl());
        try {
            minioService.deleteFile(image.getBucket(), objectName);
            log.info("Удаление изображения завершено для ownerId={}", ownerId);
        } catch (SuchResourceNotFoundEx | FileDeleteEx ex) {
            log.error("Ошибка MinIO: {}", ex.getMessage());
            throw new MinioException(ex.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при удалении изображения(500): {}", e.getMessage());
            throw e;
        }
        imageAssociationsDAO.deleteById(imageAssociation.getId());
        log.debug("Удаление записи из ImageAssociations...");

    }

    protected void updateImageRecord(Long ownerId, EntityType entityType, MultipartFile newImage) {
        if (newImage == null || newImage.isEmpty()) {
            throw new IllegalArgumentException("Новое изображение не может быть пустым");
        }

        String newObjectName;
        String bucket;
        String oldObjectName;
        String newImageUrl;

        try {
            ImageAssociation imageAssociation = imageCacheService.getImageAssociation(ownerId, entityType);
            Image oldImage = imageAssociation.getImage();

            bucket = oldImage.getBucket();
            oldObjectName = imageCacheService.extractObjectName(oldImage.getUrl());

            newObjectName = generateObjectName(entityType, newImage);

            newImageUrl = minioService.uploadFile(newImage, bucket, newObjectName);
            if (newImageUrl == null) {
                throw new FileUploadEx("Не удалось загрузить новое изображение");
            }

            Image newImageEntity = Image.builder()
                    .url(newImageUrl)
                    .filename(newObjectName)
                    .size(newImage.getSize())
                    .contentType(newImage.getContentType())
                    .bucket(bucket)
                    .originalFilename(newImage.getOriginalFilename())
                    .build();

            Image savedImage = imageDAO.save(newImageEntity);
            imageAssociation.setImage(savedImage);
            imageAssociationsDAO.save(imageAssociation);

            minioService.deleteFile(bucket, oldObjectName);
            log.info("Старое изображение успешно удалено: {}", oldObjectName);

        } catch (FileUploadEx | MinioException e) {
            log.error("Ошибка обновления изображения: {}", e.getMessage(), e);
            throw new FileUploadEx("Ошибка обновления изображения: " + e.getMessage());
        }
    }


    protected String generateObjectName(EntityType entityType, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        return entityType.name().toLowerCase() + "-" + UUID.randomUUID() + extension;
    }

    protected void updateEntityImage(Long entityId, MultipartFile image, EntityType entityType) {
        if (image != null) {
            try {
                updateImageRecord(entityId, entityType, image);
            } catch (FileUploadEx ex) {
                log.error("MinIO ошибка во время обновления фоточки для сущности: {}", entityId, ex);
                throw new RuntimeException("Ошибка обновления фотки в MinIO: " + ex.getMessage(), ex);
            }
        }
    }
    protected T getEntityById(Long entityId, JpaRepository<T, Long> repository) {
        return repository.findById(entityId)
                .orElseThrow(() -> new SuchResourceNotFoundEx(
                        String.format("Cущность с ID %d не найден(а)", entityId)
                ));
    }
}