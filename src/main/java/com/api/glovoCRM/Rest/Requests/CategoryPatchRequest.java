package com.api.glovoCRM.Rest.Requests;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CategoryPatchRequest {
    @Size(min = 3, max = 355)
    private String name;
    private MultipartFile image;
}
