package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentShortDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public abstract class EstablishmentMapper {
    @Mapping(target = "isOpen", expression = "java(establishment.getIsOpen())")
    public abstract EstablishmentDTO toDTO(Establishment establishment);

    @Mapping(target = "isOpen", expression = "java(establishment.getIsOpen())")
    public abstract EstablishmentShortDTO toShortDTO(Establishment establishment);
}
