package com.api.glovoCRM.Utils.Minio;


import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.*;
import com.api.glovoCRM.constants.MimeType;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;



@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    //todo тут надо убрать контроллер для прямой загрузки через post в minio
    private final MinioCashService minioCashService;
    private final Executor asyncExecutor;
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/svg+xml"
    );
    private static final int MAX_RETRY_ATTEMPTS = 4;
    static final String CACHE_PREFIX = "minio:";
    private final MinioClient minioClient;
    private Tika tika;

    @Value ("${minio.max-file-size-in-mb}")
    private int maxFileSizeInMB;

    @Value ("${minio.buckets.categories}")
    private String categoriesBucket;

    @Value ("${minio.buckets.establishments}")
    private String establishmentsBucket;

    @Value ("${minio.buckets.products}")
    private String productsBucket;

    @Value ("${minio.buckets.subcategories}")
    private String subcategoriesBucket;

    @PostConstruct
    public void init() {
        log.info("Инициализация бакетов...");
        initializeBucketsAsync();
        log.info("Бакеты прошли инициализацию");
        if (maxFileSizeInMB != 10) {
            log.error("Кто поменял допустимый размер файла из properties? Признавайтесь");
            throw new IllegalStateException("Кто-то поменял допустимый размер файла");
        }
    }

    private void initializeBucketsAsync() {
        List<String> buckets = Arrays.asList(
                categoriesBucket,
                establishmentsBucket,
                productsBucket,
                subcategoriesBucket
        );

        CompletableFuture<?>[] futures = buckets.stream()
                .map(this::initializeBucket)
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures)
                .exceptionally(ex -> {
                    log.error("Бакет инициализация не прошла успешно: {}", ex.getMessage());
                    return null;
                })
                .thenRun(() -> log.info("инициализайия прошла успешно"));
    }

    private CompletableFuture<Void> initializeBucket(String bucket) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!bucketExists(bucket)) { // true
                    createBucket(bucket);
                }//  метод создания бакета
                setBucketPublicPolicy(bucket); // Делаем бакет публичным
                setCorsPolicy(bucket); // Добавляем CORS

            } catch (Exception e) {
                log.error("Failed to initialize bucket {}: {}", bucket, e.getMessage());
                throw new RuntimeException("Bucket initialization failed", e);
            }
        }, asyncExecutor);
    }

    private void setCorsPolicy(String bucket) {
        try {
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucket)
                            .config("""
                                    {
                                        "Version": "2012-10-17",
                                        "Statement": [
                                            {
                                                "Effect": "Allow",
                                                "Principal": "*",
                                                "Action": ["s3:GetObject"],
                                                "Resource": ["arn:aws:s3:::%s/*"]
                                            }
                                        ]
                                    }""".formatted(bucket)).build()
            );
            log.info("CORS policy set for bucket: {}", bucket);
        } catch (Exception e) {
            log.error("Failed to set CORS for {}: {}", bucket, e.getMessage());
            throw new BucketOperationEx("Не удалось установить CORS для бакета: " + bucket);
        }
    }

    private void setBucketPublicPolicy(String bucket) {
        try {
            if (bucket == null || bucket.isEmpty()) {
                throw new IllegalArgumentException("Bucket name cannot be null or empty");
            }

            String policyJson = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": "*",
                                "Action": ["s3:GetObject"],
                                "Resource": ["arn:aws:s3:::%s/*"]
                            }
                        ]
                    }""".formatted(bucket);
            log.debug("Setting bucket policy for {}: {}", bucket, policyJson);

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucket)
                            .config(policyJson)
                            .build()
            );
            log.info("Bucket policy set to public for: {}", bucket);
        } catch (Exception e) {
            log.error("Failed to set public policy for {}: {}", bucket, e.getMessage());
            throw new BucketOperationEx("Не удалось установить политику для бакета: " + bucket);
        }
    }

    private void createBucket(String bucket) throws Exception {
        minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(bucket)
                .build());
        log.info("Bucket has been created: {}", bucket);
        setBucketPublicPolicy(bucket);
    }

    private boolean bucketExists(String bucket) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (MinioException e) {
            log.error("Ошибка доступа к MinIO при проверке бакета {}: {}", bucket, e.getMessage());
            throw new MinioConnectionEx("Ошибка подключения к MinIO: " + e.getMessage());
        } catch (Exception e) {
            log.error("Критическая ошибка при проверке бакета {}: {}", bucket, e.getMessage());
            throw new BucketOperationEx("Ошибка операции с бакетом: " + e.getMessage());
        }
    }

    @Retryable (maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff (delay = 1000, multiplier = 2),
            retryFor = {MinioConnectionEx.class, IOException.class},
            noRetryFor = {IllegalArgumentException.class, InvalidFileTypeEx.class})
    @CacheEvict (cacheNames = CACHE_PREFIX + "objects", key = "#bucketName + ':' + #objectName")
    public String uploadFile(@AllowedContentTypes (
            value = {MimeType.JPEG, MimeType.PNG, MimeType.JPG, MimeType.SVG},
            message = "Допустимые форматы: JPEG, PNG, JPG, SVG"
    ) MultipartFile file, @NotNull String bucketName, @NotNull @NotBlank String objectName) throws MinioException {
        validateFile(file);
        bucketExists(bucketName);
        if (doesObjectExist(bucketName, objectName)) {
            log.debug("Тестовый варик");
            throw new AlreadyExistsEx("Takaya photka uje sushestvuet");
        }
        try {
            byte[] fileData = file.getBytes();
            String contentType = detectContentType(file);
            uploadToMinio(bucketName, objectName, fileData, contentType);
            log.info("Файл успешно загружен: {}", objectName);
            return minioCashService.buildObjectUrl(bucketName, objectName);
        } catch (MinioConnectionEx | IOException e) {
            log.error("Ошибка подключения к MinIO: {}", e.getMessage(), e);
            throw new MinioException("Ошибка подключения к MinIO ", e.getMessage());
        } catch (FileUploadEx e) {
            log.error("Непредвиденная ошибка загрузки файла: {}", e.getMessage(), e);
            throw new MinioOperationEx("Не удалось загрузить файл: " + e.getMessage());
        } catch (Exception e) {
            log.error("Непредвиденная ошибка загрузки файла: {}", e.getMessage(), e);
            throw new FileUploadEx("Не удалось загрузить файл: " + objectName + ": " + e.getMessage());
        }
    }

    private void uploadToMinio(@NotNull String bucketName, @NotNull @NotBlank String objectName, byte[] fileData, String contentType) {
        try (InputStream is = new ByteArrayInputStream(fileData)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(is, fileData.length, -1)
                            .contentType(contentType)
                            .build());
        } catch (Exception e) {
            throw new FileUploadEx("ПРи загрузке ошибка: " + objectName);
        }
    }


//    public String getObjectUrl(String bucketName, String objectName) {
//        return buildObjectUrl(bucketName, objectName);
//    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationEx("File cannot be empty");
        }

        if (file.getSize() > maxFileSizeInMB * 1024L * 1024L) {
            throw new FileSizeEx("File size exceeds maximum allowed");
        }

        String mimeType = detectContentType(file);
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new InvalidFileTypeEx("Unsupported file type: " + mimeType);
        }
    }

    private String detectContentType(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            return tika.detect(is);
        } catch (IOException e) {
            throw new FileValidationEx("Content type detection failed");
        }
    }

    public InputStream getFile(String bucketName, String objectName) {
        bucketExists(bucketName);
        doesObjectExist(bucketName, objectName);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (MinioException | IOException | InvalidKeyException e) {
            throw new FileDownloadEx("Ошибка загрузки файла: " + e.getMessage());
        } catch (Exception e) {
            throw new FileDeleteEx("Ошибка загрузки файла: " + e.getMessage());
        }
    }

    //    private void validateBucket(String bucket) {
//        try {
//            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
//                throw new InvalidBucketEx("Бакет не найден: " + bucket);
//            }
//        } catch (Exception e) {
//            throw new BucketOperationEx("Ошибка в бакете при валидации: " + bucket);
//        }
//    }
    @Retryable (maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff (delay = 1000, multiplier = 2),
            retryFor = {MinioConnectionEx.class, IOException.class})
    @CacheEvict (cacheNames = CACHE_PREFIX + "objects", key = "#bucketName + ':' + #objectName")
    public void deleteFile(String bucketName, String objectName) {
        if (bucketExists(bucketName)) {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
                log.info("Файл {} удален", objectName);
            } catch (ErrorResponseException e) {
                if (e.errorResponse().code().equals("NoSuchKey")) {
                    throw new SuchResourceNotFoundEx("Объект не найден");
                }
                throw new FileDeleteEx("Ошибка MinIO: " + e.getMessage());
            } catch (Exception e) {
                throw new FileDeleteEx("Ошибка удаления: " + e.getMessage());
            }
        }
    }

    private boolean doesObjectExist(String bucketName, String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new MinioOperationEx("Ошибка MinIO: " + e.getMessage());
        } catch (MinioException e) {
            throw new MinioConnectionEx("Ошибка подключения: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Критическая ошибка: " + e.getMessage(), e);
        }
    }

    //    public String generateUniqueName(MultipartFile file) { ////minioController чисто
//        String originalFilename = file.getOriginalFilename();
//        if (originalFilename == null) {
//            throw new FileValidationEx("Файл не имеет имени");
//        }
//        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        return UUID.randomUUID() + extension;
//    }
//    public boolean doesFileExistByContent(String bucketName, String objectName, MultipartFile newFile) {
//        try (InputStream existing = getFile(bucketName, objectName);
//             InputStream newStream = newFile.getInputStream()) {
//
//            return Arrays.equals(
//                    minioCashService.calculateHash(existing),
//                    minioCashService.calculateHash(newStream)
//            );
//        } catch (Exception e) {
//            log.error("При проверке произошла ошибка", e);
//            return false;
//        }
//    }
    @Bean
    public Tika tika() {
        this.tika = new Tika();
        return this.tika;
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("MinioInit-");
        executor.initialize();
        return executor;
    }
}