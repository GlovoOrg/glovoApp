package com.api.glovoCRM.Rest.Requests.CategoryRequests;

import com.api.glovoCRM.Minio.AllowedContentTypes;
import com.api.glovoCRM.constants.MimeType;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CategoryPatchRequest {
    Long id;
    @Size(min = 3, max = 355)
    private String name;
    @AllowedContentTypes({MimeType.JPEG, MimeType.PNG, MimeType.SVG, MimeType.JPG})
    private MultipartFile image;
}
