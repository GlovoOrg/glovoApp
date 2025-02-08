package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentFilterDTO;
import com.api.glovoCRM.Models.EstablishmentModels.EstablishmentFilter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface EstablishmentFilterMapper extends BaseMapper<EstablishmentFilter, EstablishmentFilterDTO> {

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "products", source = "products")
    EstablishmentFilterDTO toDTO(EstablishmentFilter establishmentFilter);

    @Override
    default List<EstablishmentFilterDTO> toDTOList(List<EstablishmentFilter> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }
}
