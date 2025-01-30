package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import com.api.glovoCRM.Models.EstablishmentModels.ImageAssociation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private List<SubCategoryDTO> subCategories;
}
