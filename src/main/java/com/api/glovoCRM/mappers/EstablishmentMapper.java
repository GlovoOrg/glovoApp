package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.ProductDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, EstablishmentFilterMapper.class, EstablishmentAddressMapper.class})
public interface EstablishmentMapper extends BaseMapper<Establishment, EstablishmentDTO> {
    Logger log = LoggerFactory.getLogger(EstablishmentMapper.class);
    @Override
    @Mapping(target = "subcategoryId", source = "subcategory.id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "priceOfDelivery", source = "priceOfDelivery")
    @Mapping(target = "timeOfDelivery", source = "timeOfDelivery")
//    @Mapping(target = "isOpen", expression = "java(isOpen(establishment))")
    @Mapping(target = "openNow", expression = "java(isOpen(establishment))")
    @Mapping(target = "rating", expression = "java(establishment.getRating())")
    @Mapping(target = "quantityOfRatings", source = "quantityOfRatings")
    @Mapping(target = "openTime", source = "openTime")
    @Mapping(target = "closeTime", source = "closeTime")
    @Mapping(target = "products", expression = "java(getAllProducts(establishment))")
    @Mapping(target = "establishmentFilters", source = "establishment_filters")
    @Mapping(target = "imageUrl", expression = "java(com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil.getImageUrl(establishment.getId(), com.api.glovoCRM.constants.EntityType.Establishment))")
    @Mapping(target = "establishmentAddress", source = "establishmentAddress")
    EstablishmentDTO toDTO(Establishment establishment);

    @Named("toEstablishmentWithoutProducts")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "priceOfDelivery", source = "priceOfDelivery", ignore = true)
    @Mapping(target = "timeOfDelivery", source = "timeOfDelivery")
    @Mapping(target = "openNow", expression = "java(isOpen(establishment))")
//    @Mapping(target = "isOpen", expression = "java(isOpen(establishment))")
    @Mapping(target = "rating", expression = "java(establishment.getRating())")
    @Mapping(target = "quantityOfRatings", source = "quantityOfRatings")
    @Mapping(target = "openTime", source = "openTime", ignore = true)
    @Mapping(target = "closeTime", source = "closeTime", ignore = true)
    @Mapping(target = "products", source = "products",ignore = true)
    @Mapping(target = "establishmentFilters", source = "establishment_filters", ignore = true)
    @Mapping(target = "imageUrl", expression = "java(com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil.getImageUrl(establishment.getId(), com.api.glovoCRM.constants.EntityType.Establishment))")
    @Mapping(target = "establishmentAddress", source = "establishmentAddress",ignore = true)
    @Mapping(target = "subcategoryId", source = "subcategory.id")
    EstablishmentDTO toEstablishmentWithoutProducts(Establishment establishment);

    default List<ProductDTO> getAllProducts(Establishment establishment) {
        ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

        List<ProductDTO> allProducts = new ArrayList<>(productMapper.toDTOList(establishment.getProducts()));

        establishment.getEstablishment_filters().forEach(filter ->
                allProducts.addAll(productMapper.toDTOList(filter.getProducts()))
        );

        return allProducts;
    }

    @Named("toEstablishmentWithoutProductsList")
    default List<EstablishmentDTO> toEstablishmentWithoutProductsList(List<Establishment> establishments) {
        if (establishments == null) {
            return null;
        }
        return establishments.stream()
                .map(this::toEstablishmentWithoutProducts)
                .toList();
    }

    default List<EstablishmentDTO> toDTOList(List<Establishment> establishments) {
        return establishments.stream()
                .map(this::toDTO)
                .toList();
    }
    default Boolean isOpen(Establishment establishment) {
        ZoneId zoneId = ZoneId.of("Asia/Bishkek");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        LocalTime currentTime = now.toLocalTime();

        LocalTime openTime = establishment.getOpenTime();
        LocalTime closeTime = establishment.getCloseTime();

        if (openTime == null || closeTime == null) {
            return false;
        }
        if (closeTime.isBefore(openTime)) {
            return currentTime.isAfter(openTime) || currentTime.isBefore(closeTime);
        } else {
            return currentTime.isAfter(openTime) && currentTime.isBefore(closeTime);
        }
    }
}

