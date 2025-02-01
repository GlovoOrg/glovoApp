package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.Data;

import java.util.List;

@Data
public class SubCategoryDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String imageUrl;
    private List<EstablishmentDTO> establishments;
}
