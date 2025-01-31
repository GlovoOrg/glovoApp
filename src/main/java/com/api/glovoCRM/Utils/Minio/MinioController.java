package com.api.glovoCRM.Utils.Minio;

import com.api.glovoCRM.Rest.Responses.FileUploadResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.time.LocalDateTime;

import com.api.glovoCRM.constants.MimeType;

@Validated
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class MinioController {
    private final MinioService minioService;

    @PostMapping("/upload/{bucket}")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable String bucket,
            @RequestParam("file") @Valid @AllowedContentTypes({MimeType.JPEG, MimeType.PNG, MimeType.JPG, MimeType.SVG}) MultipartFile file
    ) {
        String objectName = minioService.generateUniqueName(file);
        String url = minioService.uploadFile(file, bucket, objectName);
        LocalDateTime uploadedAt = LocalDateTime.now();
        return ResponseEntity.ok(
                new FileUploadResponse(url, objectName, bucket, uploadedAt)
        );
    }

    @GetMapping("/{bucket}/{objectName}")
    public void getFile(
            @PathVariable String bucket,
            @PathVariable String objectName,
            HttpServletResponse response
    ) {
        try (InputStream is = minioService.getFile(bucket, objectName)) {
            String contentType = minioService.getContentType(bucket, objectName);
            response.setContentType(contentType);
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @DeleteMapping("/{bucket}/{objectName}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String bucket,
            @PathVariable String objectName
    ) {
        minioService.deleteFile(bucket, objectName);
        return ResponseEntity.noContent().build();
    }
}