package com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryDTO {
    private CategoryDTO category;
    private String imageUrl;
    private List<EstablishmentDTO> establishments;
}
