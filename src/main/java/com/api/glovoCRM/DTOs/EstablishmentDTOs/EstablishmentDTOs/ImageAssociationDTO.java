package com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.BaseDTO;
import com.api.glovoCRM.constants.EntityType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageAssociationDTO {
    private Long id;
    private ImageDTO image;
    private EntityType entityType;
    private Long ownerId;
}
