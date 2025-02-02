package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs.EstablishmentDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs.EstablishmentShortDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface EstablishmentMapper {

    @Mapping(target = "isOpen", expression = "java(establishment.getIsOpen())")
    @Mapping(target = "products", source = "products")
    EstablishmentDTO toDTO(Establishment establishment);

    @Mapping(target = "isOpen", expression = "java(establishment.getIsOpen())")
    EstablishmentShortDTO toShortDTO(Establishment establishment);
}

