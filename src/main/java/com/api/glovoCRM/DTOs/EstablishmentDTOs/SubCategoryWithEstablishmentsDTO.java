package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@EqualsAndHashCode (callSuper = true)
@Data
public class SubCategoryWithEstablishmentsDTO extends BaseDTO{
    private Long categoryId;
    private String categoryName;
    private List<EstablishmentDTO> establishments;
}
