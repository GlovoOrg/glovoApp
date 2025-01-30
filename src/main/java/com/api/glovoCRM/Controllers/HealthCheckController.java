package com.api.glovoCRM.Controllers;

import io.minio.MinioClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    private final MinioClient minioClient;

    public HealthCheckController(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        try {
            minioClient.listBuckets();
            return ResponseEntity.ok("Service is UP");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Minio connection error: " + e.getMessage());
        }
    }
}
