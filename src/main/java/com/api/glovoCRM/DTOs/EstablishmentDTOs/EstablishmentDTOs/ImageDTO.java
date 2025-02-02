package com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.BaseDTO;
import com.api.glovoCRM.constants.EntityType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO extends BaseDTO {
    private String url;
    private String filename;
    private Long size;
    private EntityType contentType;
    private String bucket;
    private String originalFilename;
}
