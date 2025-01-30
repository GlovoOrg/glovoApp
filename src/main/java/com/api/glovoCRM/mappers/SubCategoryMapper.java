package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.SubCategoryDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil;
import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EstablishmentMapper.class})
public interface SubCategoryMapper {
     @Mapping(target = "establishments", source = "establishments")
     @Mapping(target = "imageUrl", expression = "java(com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil.getImageUrl(subCategory.getId(), com.api.glovoCRM.constants.EntityType.SubCategory))")
     SubCategoryDTO toDTO(SubCategory subCategory);
}
