package com.api.glovoCRM.Utils.Minio;


import com.api.glovoCRM.Exceptions.MinioExceptions.*;
import com.api.glovoCRM.constants.MimeType;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioCashService minioCashService;
    private final Executor asyncExecutor;
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/svg+xml"
    );
    private static final int MAX_RETRY_ATTEMPTS = 4;
    private static final String CACHE_PREFIX = "minio:";
    private final MinioClient minioClient;
    private Tika tika;

    @Value("${minio.max-file-size-in-mb}")
    private int maxFileSizeInMB;
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
        initializeBucketsAsync();
        log.info("MinIO buckets initialized");
        if(maxFileSizeInMB != 10){
            log.error("Кто поменял допустимый размер файла из properties? Признавайтесь");
            throw  new IllegalStateException("Кто-то поменял допустимый размер файла");
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
                    log.error("Bucket initialization failed: {}", ex.getMessage());
                    return null;
                })
                .thenRun(() -> log.info("All buckets initialized successfully"));
    }

    private CompletableFuture<Void> initializeBucket(String bucket) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!bucketExists(bucket)) {
                    createBucket(bucket);
                    setBucketPublicPolicy(bucket); // Делаем бакет публичным
                    setCorsPolicy(bucket); // Добавляем CORS
                }
            } catch (Exception e) {
                log.error("Failed to initialize bucket {}: {}", bucket, e.getMessage());
                throw new RuntimeException("Bucket initialization failed", e);
            }
        }, asyncExecutor);
    }
    private void setCorsPolicy(String bucket) {
        try {
            String corsPolicy = """
        {
          "CORSRules": [
            {
              "AllowedHeaders": ["*"],
              "AllowedMethods": ["GET", "POST", "PUT", "DELETE"],
              "AllowedOrigins": ["*"],
              "ExposeHeaders": ["ETag"]
            }
          ]
        }""";

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucket)
                            .config(corsPolicy)
                            .build()
            );

            log.info("CORS policy set for bucket: {}", bucket);
        } catch (Exception e) {
            log.error("Failed to set CORS for {}: {}", bucket, e.getMessage());
        }
    }

    private void setBucketPublicPolicy(String bucket) {
        try {
            String policyJson = """
        {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": "*",
                    "Action": [
                        "s3:GetObject"
                    ],
                    "Resource": [
                        "arn:aws:s3:::%s/*"
                    ]
                }
            ]
        }""".formatted(bucket);

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucket)
                            .config(policyJson)
                            .build()
            );

            log.info("Бакет теперь публичный: {}", bucket);
        } catch (Exception e) {
            log.error("Ошибка при установке публичного доступа для {}: {}", bucket, e.getMessage());
        }
    }

    private void createBucket(String bucket) throws Exception {
        minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(bucket)
                .build());
        log.info("Bucket has been created: {}", bucket);
    }

    private boolean bucketExists(String bucket) throws BucketCreationEx{
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            throw new BucketOperationEx("Bucket check failed");
        }
    }
    @Retryable(maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {MinioConnectionEx.class, IOException.class},
    noRetryFor = {IllegalArgumentException.class, InvalidFileTypeEx.class})
    @CacheEvict(cacheNames = CACHE_PREFIX + "objects", key = "#bucketName + ':' + #objectName")
    @Transactional
    public String uploadFile(    @AllowedContentTypes(
            value = {MimeType.JPEG, MimeType.PNG, MimeType.JPG, MimeType.SVG},
            message = "Допустимые форматы: JPEG, PNG, JPG, SVG"
    ) MultipartFile file, @NotNull String bucketName,@NotNull @NotBlank String objectName) {

        validateBucket(bucketName);

        if (validateObjectInBucket(bucketName, objectName)) {
            log.info("Файл уже существует: {}", objectName);
            return buildObjectUrl(bucketName, objectName);
        }
        validateFile(file);
        try {
            byte[] fileData = file.getBytes();
            String contentType = detectContentType(file);

            uploadToMinio(bucketName, objectName, fileData, contentType);
            log.info("Файл успешно загружен: {}", objectName);
            return buildObjectUrl(bucketName, objectName);
        } catch (Exception e) {
            log.error("Ошибка загрузки файла: {}", e.getMessage(), e);
            throw new FileUploadEx("Не удалось загрузить файл: " + e.getMessage());
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
    public String buildObjectUrl(String bucketName, String objectName) {
        return String.format("%s/%s/%s", minioEndpoint, bucketName, objectName);
    }

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

    public boolean validateObjectInBucket(String bucketName, String objectName) {
        try {
            validateBucket(bucketName);
            log.info("Проверка существования объекта в бакете: bucket={}, object={}", bucketName, objectName);
            return doesObjectExist(bucketName, objectName);
        } catch (Exception e) {
            log.error("Ошибка при проверке существования объекта: {}", e.getMessage());
            return false;
        }
    }
    private void validateBucket(String bucket) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                throw new InvalidBucketEx("Бакет не найден: " + bucket);
            }
        } catch (Exception e) {
            throw new BucketOperationEx("Ошибка в бакете при валидации: " + bucket);
        }
    }
    @CacheEvict(cacheNames = CACHE_PREFIX + "objects", key = "#bucketName + ':' + #objectName")
    public void deleteFile(String bucketName, String objectName) {
        validateObjectInBucket(bucketName, objectName);
        try {
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
    public boolean doesObjectExist(String bucketName, String objectName) {
        log.info("Загрузка данных из MinIO для bucket: {}, object: {}", bucketName, objectName);
        try {
            minioClient.statObject(
                    io.minio.StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return false;
            }
            log.error("Ошибка при проверке существования файла: {}", e.getMessage());
            throw new RuntimeException("Ошибка проверки существования файла: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при проверке существования файла: {}", e.getMessage());
            throw new RuntimeException("Непредвиденная ошибка проверки существования файла", e);
        }
    }
    public String generateUniqueName(MultipartFile file) { ////minioController чисто
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new FileValidationEx("Файл не имеет имени");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    }
    public boolean doesFileExistByContent(String bucketName, String objectName, MultipartFile newFile) {
        try (InputStream existing = getFile(bucketName, objectName);
             InputStream newStream = newFile.getInputStream()) {

            return Arrays.equals(
                    minioCashService.calculateHash(existing),
                    minioCashService.calculateHash(newStream)
            );
        } catch (Exception e) {
            log.error("При проверке произошла ошибка", e);
            return false;
        }
    }
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