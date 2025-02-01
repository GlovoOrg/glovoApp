package com.api.glovoCRM.Rest.Requests.SubCategoryRequests;

import com.api.glovoCRM.Rest.Requests.BaseRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class SubCategoryCreateRequest extends BaseRequest {
    @NotNull(message = "ID категории обязательно")
    @Positive(message = "Id категории должно быть положительным")
    private Long categoryId;
}
