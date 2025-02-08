package com.api.glovoCRM.Rest.Requests.SubCategoryRequests;

import com.api.glovoCRM.Rest.Requests.BaseRequest;
import com.api.glovoCRM.Utils.Minio.AllowedContentTypes;
import com.api.glovoCRM.constants.MimeType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;
@EqualsAndHashCode(callSuper = true)
@Data
public class SubCategoryPatchRequest extends BaseRequest {
    @Positive(message = "ID категории должно быть положительным числом")
    private Long categoryId;
}
