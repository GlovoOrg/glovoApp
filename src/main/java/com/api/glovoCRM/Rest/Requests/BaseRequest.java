package com.api.glovoCRM.Rest.Requests;

import com.api.glovoCRM.Utils.Minio.AllowedContentTypes;
import com.api.glovoCRM.constants.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public abstract class BaseRequest {
    @NotBlank(message = "Название не может быть null")
    @Size(min = 3, max = 355, message = "Название обязательно от 3 до 355 символов")
    private String name;

    @NotNull(message = "Изображение обязательно")
    @AllowedContentTypes(
            value = {MimeType.JPEG, MimeType.PNG, MimeType.JPG, MimeType.SVG},
            message = "Допустимые форматы: JPEG, PNG, JPG, SVG"
    )
    private MultipartFile image;
}
