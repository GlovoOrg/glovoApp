package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.Data;

import java.util.List;

@Data
public class SubCategoryDTO {
    private Long id;
    private String name;
    private CategoryDTO category;
    private String imageUrl;
    private List<EstablishmentDTO> establishments;
}
