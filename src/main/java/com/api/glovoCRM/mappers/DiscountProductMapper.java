package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.DiscountProductDTO;
import com.api.glovoCRM.Models.EstablishmentModels.DiscountProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiscountProductMapper{
    @Mapping(target = "id", source = "id")
    @Mapping(target = "discount", source = "discount")
    @Mapping(target = "active", source = "active")
    DiscountProductDTO toDTO(DiscountProduct discountProduct);
}
