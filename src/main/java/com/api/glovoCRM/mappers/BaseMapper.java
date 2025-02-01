package com.api.glovoCRM.mappers;

import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface BaseMapper<ENTITY, DTO> {
    DTO toDTO(ENTITY entity);
    List<DTO> toDTOList(List<ENTITY> entities);
}
