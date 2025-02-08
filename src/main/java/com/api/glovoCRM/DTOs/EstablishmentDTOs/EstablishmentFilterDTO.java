package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstablishmentFilterDTO {
    private Long id;
    @NotBlank(message = "Название фильтра обязательно")
    @Size(max = 355, message = "Максимальная длина названия — 355 символов")
    @Column(name = "name", length = 355, nullable = false)
    private String name;
    private List<ProductDTO> products;
}
