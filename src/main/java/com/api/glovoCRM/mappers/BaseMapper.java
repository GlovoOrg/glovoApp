package com.api.glovoCRM.mappers;

import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface BaseMapper<ENTITY, DTO> {
    DTO toDTO(ENTITY entity);
    default List<DTO> toDTOList(List<ENTITY> entities){
        if(entities == null || entities.isEmpty()) return null;
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }
}
