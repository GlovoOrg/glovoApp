package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentAddressDTO;
import com.api.glovoCRM.Models.EstablishmentModels.EstablishmentAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EstablishmentAddressMapper{
    @Mapping(target = "id", source = "id")
    @Mapping(target = "addressLine", source = "addressLine")
    @Mapping(target = "latitude", source = "latitude")
    @Mapping(target = "longitude", source = "longitude")
    EstablishmentAddressDTO toDTO(EstablishmentAddress establishmentAddress);
}
