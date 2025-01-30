package com.api.glovoCRM.Minio;

import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileDeleteEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileDownloadEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileUploadEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileValidationEx;
import com.api.glovoCRM.Models.EstablishmentModels.Image;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private final ImageDAO imageDAO;
    private final MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.buckets.categories}")
    private String categoriesBucket;

    @Value("${minio.buckets.establishments}")
    private String establishmentsBucket;

    @Value("${minio.buckets.products}")
    private String productsBucket;

    @Value("${minio.buckets.subcategories}")
    private String subcategoriesBucket;

    @PostConstruct
    public void init() {
        log.info("Initializing MinIO buckets...");
        createBucketIfNotExists(categoriesBucket);
        createBucketIfNotExists(establishmentsBucket);
        createBucketIfNotExists(productsBucket);
        createBucketIfNotExists(subcategoriesBucket);
        log.info("MinIO buckets initialized");
    }

    private void createBucketIfNotExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("Bucket {} created", bucketName);
            } else {
                log.debug("Bucket {} already exists", bucketName);
            }
        } catch (Exception e) {
            log.error("Error creating bucket {}: {}", bucketName, e.getMessage());
            throw new RuntimeException("Failed to create bucket: " + bucketName, e);
        }
    }

    public String uploadFile(MultipartFile file, String bucketName, String objectName) {
        try {
            byte[] fileData = file.getBytes();

            String detectedType = new Tika().detect(fileData);

            try (InputStream inputStream = new ByteArrayInputStream(fileData)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, fileData.length, -1)
                                .contentType(detectedType)
                                .build()
                );
            }

            return String.format("%s/%s/%s", minioEndpoint, bucketName, objectName);
        } catch (Exception e) {
            log.error("Ошибка загрузки файла: {}", e.getMessage(), e);
            throw new FileUploadEx("Не удалось загрузить файл: " + e.getMessage());
        }
    }

    public InputStream getFile(String bucketName, String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new FileDeleteEx("Ошибка загрузки файла: " + e.getMessage());
        }
    }

    public void deleteFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("File deletion failed", e);
        }
    }
    public String generateUniqueName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new FileValidationEx("Файл не имеет имени");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    }
    public Image saveImage(Image image){
            if (imageDAO.existsByBucketAndOriginalFilename(image.getBucket(), image.getOriginalFilename())){
                throw new AlreadyExistsEx("Такое изображение по имени уже существует в bucket: " + image.getBucket());
            }
            return imageDAO.save(image);
    }
    public void deleteImage(Image image) {
        try {
            if (imageDAO.existsByBucketAndOriginalFilename(image.getBucket(), image.getOriginalFilename())) {
                String objectName = image.getFilename();
                log.debug("Удаление файла из MinIO: {}", objectName);

                deleteFile(image.getBucket(), objectName);
                imageDAO.delete(image);

                log.info("Изображение удалено: {}", objectName);
            } else {
                throw new RuntimeException("Такое изображение уже не существует в bucket: " + image.getBucket());
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении изображения: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить изображение", e);
        }
    }
    public String getContentType(String bucketName, String objectName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return stat.contentType();
        } catch (Exception e) {
            throw new FileDownloadEx("Ошибка получения информации о файле: " + e.getMessage());
        }
    }
}
