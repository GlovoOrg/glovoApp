package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseDTO {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 3, max = 355, message = "Имя должно быть от 3 до 355 символов")
    private String name;
    @NotBlank(message = "Url не может быть пустым")
    private String imageUrl;
}
