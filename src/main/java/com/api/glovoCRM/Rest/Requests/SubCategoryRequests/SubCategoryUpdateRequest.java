package com.api.glovoCRM.Rest.Requests.SubCategoryRequests;

import com.api.glovoCRM.Rest.Requests.BaseRequest;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubCategoryUpdateRequest extends BaseRequest {
    @Positive(message = "ID категории должно быть положительным числом")
    private Long categoryId;
}
