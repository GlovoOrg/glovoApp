package com.api.glovoCRM.Rest.Requests.CategoryRequests;

import com.api.glovoCRM.Rest.Requests.BaseRequest;
import com.api.glovoCRM.Rest.Requests.BaseRequestNotNull;
import com.api.glovoCRM.Utils.Minio.AllowedContentTypes;
import com.api.glovoCRM.constants.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;
@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryCreateRequest extends BaseRequestNotNull {

}
