package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@EqualsAndHashCode (callSuper = true)
@Data
public class CategoryWithSubcategoriesAndEstablishmentsDTO extends BaseDTO {
    private List<SubCategoryWithEstablishmentsDTO> subCategories;
}
