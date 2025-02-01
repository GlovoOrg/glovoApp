package com.api.glovoCRM.Rest.Requests.CategoryRequests;

import com.api.glovoCRM.Rest.Requests.BaseRequest;
import com.api.glovoCRM.Utils.Minio.AllowedContentTypes;
import com.api.glovoCRM.constants.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;


@Data
public class CategoryPatchRequest  {
    @Size(min = 3, max = 355)
    private String name;
    @AllowedContentTypes(value = {MimeType.JPEG, MimeType.PNG, MimeType.SVG, MimeType.JPG}, message = "Только изображения друг, без лабуды, такой как tiff и bmp")
    private MultipartFile image;
}
