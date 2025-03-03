package com.api.glovoCRM.mappers;

import com.api.glovoCRM.Repositories.CategoryRepository;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.SubCategoryDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.SubCategoryWithEstablishmentsDTO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EstablishmentMapper.class})
public abstract class SubCategoryMapper implements BaseMapper<SubCategory, SubCategoryDTO>{

     @Autowired
     private CategoryRepository categoryRepository;


     @Override
     @Mapping(target = "categoryId", source = "category.id")
     @Mapping(target = "categoryName", source = "category", qualifiedByName = "getCategoryName")
     @Mapping(target = "establishments", source = "establishments")
     @Mapping(target = "imageUrl", expression = "java(com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil.getImageUrl(subCategory.getId(), com.api.glovoCRM.constants.EntityType.SubCategory))")
     public abstract SubCategoryDTO toDTO(SubCategory subCategory);

     @Named("getCategoryName")
     protected String getCategoryName(Category category) {
          if (category == null) {
               return null;
          }
          return categoryRepository.findById(category.getId())
                  .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"))
                  .getName();
     }

     @Override
     public List<SubCategoryDTO> toDTOList(List<SubCategory> subCategories) {
          return subCategories.stream()
                  .map(this::toDTO)
                  .toList();
     }

     @Named("toSubCategoryWithoutEstablishments")
     @Mapping(target = "categoryId", source = "category.id")
     @Mapping(target = "categoryName", source = "category", qualifiedByName = "getCategoryName")
     @Mapping(target = "establishments", ignore = true)
     @Mapping(target = "imageUrl", expression = "java(com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil.getImageUrl(subCategory.getId(), com.api.glovoCRM.constants.EntityType.SubCategory))")
     abstract SubCategoryDTO toSubCategoryWithoutEstablishments(SubCategory subCategory);

     @Named("toSubCategoryWithEstablishments")
     @Mapping(target = "categoryId", source = "category.id")
     @Mapping(target = "categoryName", source = "category", qualifiedByName = "getCategoryName")
     @Mapping(target = "establishments", source = "establishments", qualifiedByName = "toEstablishmentWithoutProductsList")
     @Mapping(target = "imageUrl", expression = "java(com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil.getImageUrl(subCategory.getId(), com.api.glovoCRM.constants.EntityType.SubCategory))")
     abstract SubCategoryWithEstablishmentsDTO toSubCategoryWithEstablishments(SubCategory subCategory);
}
