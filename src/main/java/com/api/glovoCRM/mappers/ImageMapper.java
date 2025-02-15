package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.ImageDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    @Mapping(target = "url", source = "url")
    @Mapping(target = "filename", source = "filename")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "contentType", source = "contentType")
    @Mapping(target = "bucket", source = "bucket")
    @Mapping(target = "originalFilename", source = "originalFilename")
    ImageDTO toDTO(Image image);
}
