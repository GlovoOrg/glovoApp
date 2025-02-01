package com.api.glovoCRM.Utils.Minio;

import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileDeleteEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileDownloadEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileUploadEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileValidationEx;
import com.api.glovoCRM.Models.EstablishmentModels.Image;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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
            if (!doesObjectExist(bucketName, objectName)) {
                throw new SuchResourceNotFoundEx("Файл не найден");
            }
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("Файл {} удален", objectName);
        } catch (Exception e) {
            log.error("Ошибка при удалении файла {}: {}", objectName, e.getMessage());
            throw new FileDeleteEx("Ошибка при удалении файла: " + e.getMessage());
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
//    public void deleteImage(Image image) {
//        try {
//            if (imageDAO.existsByBucketAndOriginalFilename(image.getBucket(), image.getOriginalFilename())) {
//                String objectName = image.getFilename();
//                log.debug("Удаление файла из MinIO: {}", objectName);
//
//                deleteFile(image.getBucket(), objectName);
//                imageDAO.delete(image);
//
//                log.info("Изображение удалено: {}", objectName);
//            } else {
//                throw new RuntimeException("Такое изображение уже не существует в bucket: " + image.getBucket());
//            }
//        } catch (Exception e) {
//            log.error("Ошибка при удалении изображения: {}", e.getMessage(), e);
//            throw new RuntimeException("Не удалось удалить изображение", e);
//        }
//    } Данный метод оставим на будущее, без него вроде как норм работает, использовал для эксперимента.
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
    public boolean doesObjectExist(String bucketName, String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new FileDownloadEx("Ошибка проверки существования файла: " + e.getMessage());
        } catch (Exception e) {
            throw new FileDownloadEx("Ошибка проверки существования файла: " + e.getMessage());
        }
    }
    public boolean doesFileExistByContent(String bucketName, String objectName, MultipartFile newFile) {
        try {
            InputStream existingFileStream = minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucketName).object(objectName).build()
            );

            byte[] existingHash = getSHA256(existingFileStream);
            byte[] newFileHash = getSHA256(newFile.getInputStream());

            return Arrays.equals(existingHash, newFileHash);
        } catch (Exception e) {
            log.error("Ошибка при проверке содержимого файла {}: {}", objectName, e.getMessage());
            return false;
        }
    }

    private byte[] getSHA256(InputStream inputStream) throws NoSuchAlgorithmException, IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }
        return digest.digest();
    }
}
