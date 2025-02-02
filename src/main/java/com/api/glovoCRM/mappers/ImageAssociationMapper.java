package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs.ImageAssociationDTO;
import com.api.glovoCRM.Models.EstablishmentModels.ImageAssociation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ImageMapper.class}) // Убрали CategoryMapper
public interface ImageAssociationMapper {
    @Mapping(target = "image", source = "image")
    ImageAssociationDTO toDTO(ImageAssociation imageAssociation);
}