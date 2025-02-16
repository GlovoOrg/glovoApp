package com.api.glovoCRM.Rest.Requests.EstablishmentFilterRequests;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.ProductDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class EstablishmentFilterUpdateRequest {
    @NotBlank(message = "Название фильтра обязательно")
    @Size(max = 355, message = "Максимальная длина названия — 355 символов")
    private String name;

    @NotNull(message = "Id заведения обязательно")
    private Long establishmentId;

    private List<ProductDTO> products;
}
