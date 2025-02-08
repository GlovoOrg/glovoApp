package com.api.glovoCRM.Rest.Requests.SubCategoryRequests;


import com.api.glovoCRM.Rest.Requests.BaseRequestNotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubCategoryUpdateRequest extends BaseRequestNotNull {
    @Positive(message = "ID категории должно быть положительным числом")
    private Long categoryId;
}
