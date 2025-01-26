package com.api.glovoCRM.Minio;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class MinioController {

    private final MinioService minioService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return minioService.uploadFile(file, "products", "product-123.jpg");
    }

    @GetMapping("/{objectName}")
    public void getFile(@PathVariable String objectName, HttpServletResponse response) {
        try (InputStream is = minioService.getFile("products", objectName)) {
            response.setContentType("image/jpeg");
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @DeleteMapping("/{objectName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String objectName) {
        minioService.deleteFile("products", objectName);
        return ResponseEntity.noContent().build();
    }
}
