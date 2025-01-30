package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.CategoryDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring", uses = {SubCategoryMapper.class})
public interface CategoryMapper {
    @Mapping(target = "subCategories", source = "subCategories")
    @Mapping(target = "imageUrl", expression = "java(com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil.getImageUrl(category.getId(), com.api.glovoCRM.constants.EntityType.Category))")
    CategoryDTO toDTO(Category category);
}