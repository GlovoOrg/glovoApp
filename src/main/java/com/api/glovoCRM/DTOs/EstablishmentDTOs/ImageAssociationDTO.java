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
public class ImageAssociationDTO {
    private Long id;
    private ImageDTO image;
    private EntityType entityType;
    private Long ownerId;
}
