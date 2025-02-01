package com.api.glovoCRM.Rest.Requests.SubCategoryRequests;

import com.api.glovoCRM.Utils.Minio.AllowedContentTypes;
import com.api.glovoCRM.constants.MimeType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class SubCategoryPatchRequest {

    @Size(max = 255, message = "Имя должно содержать не более 255 символов")
    private String name;

    @Positive(message = "ID категории должно быть положительным числом")
    private Long categoryId;

    @AllowedContentTypes(value = {MimeType.JPEG, MimeType.PNG, MimeType.SVG, MimeType.JPG}, message = "Только изображения друг, без лабуды, такой как tiff и bmp")
    private MultipartFile image;
}
