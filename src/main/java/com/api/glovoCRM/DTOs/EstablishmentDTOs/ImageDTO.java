package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import com.api.glovoCRM.constants.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private Long id;
    private String url;
    private String filename;
    private Long size;
    private EntityType contentType;
    private String bucket;
    private String originalFilename;
}
