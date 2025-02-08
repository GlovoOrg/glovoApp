package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentShortDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, EstablishmentFilterMapper.class, EstablishmentAddressMapper.class})
public interface EstablishmentMapper extends BaseMapper<Establishment, EstablishmentDTO> {

    @Override
    @Mapping(target = "name", source = "name")
    @Mapping(target = "priceOfDelivery", source = "priceOfDelivery")
    @Mapping(target = "timeOfDelivery", source = "timeOfDelivery")
    @Mapping(target = "isOpen", expression = "java(establishment.getIsOpen())")
    @Mapping(target = "rating", expression = "java(establishment.getRating())")
    @Mapping(target = "quantityOfRatings", source = "quantityOfRatings")
    @Mapping(target = "openTime", source = "openTime")
    @Mapping(target = "closeTime", source = "closeTime")
    @Mapping(target = "products", source = "products")
    @Mapping(target = "establishmentFilters", source = "establishment_filters")
    @Mapping(target = "imageUrl", expression = "java(com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil.getImageUrl(establishment.getId(), com.api.glovoCRM.constants.EntityType.Establishment))")
    @Mapping(target = "establishmentAddressDTO", source = "establishmentAddress")
    EstablishmentDTO toDTO(Establishment establishment);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "rating", expression = "java(establishment.getRating())")
    @Mapping(target = "isOpen", expression = "java(establishment.getIsOpen())")
    @Mapping(target = "imageUrl", expression = "java(com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil.getImageUrl(establishment.getId(), com.api.glovoCRM.constants.EntityType.Establishment))")
    @Mapping(target = "priceOfDelivery", source = "priceOfDelivery")
    @Mapping(target = "timeOfDelivery", source = "timeOfDelivery")
    @Mapping(target = "quantityOfRatings", source = "quantityOfRatings")
    EstablishmentShortDTO toShortDTO(Establishment establishment);

    default List<EstablishmentShortDTO> toShortDTOList(List<Establishment> establishments) {
        if (establishments == null) {
            return null;
        }
        return establishments.stream()
                .map(this::toShortDTO)
                .toList();
    }

    @Override
    default List<EstablishmentDTO> toDTOList(List<Establishment> establishments) {
        return establishments.stream()
                .map(this::toDTO)
                .toList();
    }
}

