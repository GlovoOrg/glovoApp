package com.api.glovoCRM.Utils.Minio;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioCashService {
    private final MinioClient minioClient;
    @Value("${minio.buffer}")
    private int bufferSize;

    @Cacheable(cacheNames = "minio:metadata", key = "#bucketName + ':' + #objectName")
    public String getContentType(String bucketName, String objectName) {
        try {
            var stat = minioClient.statObject(
                    io.minio.StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return stat.contentType();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения информации о файле: " + e.getMessage());
        }
    }

    public byte[] calculateHash(InputStream input) {
        // Новый метод для работы с InputStream
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка вычисления хеша: " + e.getMessage());
        }
        //    @Cacheable(cacheNames = "minio:hashes", key = "#bucketName + ':' + #objectName")
//    public byte[] calculateHash(String bucketName, String objectName) {
//        try (InputStream input = minioClient.getObject(
//                io.minio.GetObjectArgs.builder()
//                        .bucket(bucketName)
//                        .object(objectName)
//                        .build()
//        )) {
//            return calculateHash(input);
//        } catch (Exception e) {
//            throw new RuntimeException("Ошибка вычисления хеша файла: " + e.getMessage());
//        }
//    }
    }
}
