package com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO extends BaseDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private List<SubCategoryDTO> subCategories;
}
