package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryDTO extends BaseDTO{
    private Long categoryId;
    private String categoryName;
    private List<EstablishmentDTO> establishments;
}
