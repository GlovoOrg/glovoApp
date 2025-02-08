package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import com.api.glovoCRM.Models.EstablishmentModels.ImageAssociation;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO extends BaseDTO{
    private List<SubCategoryDTO> subCategories;
}
