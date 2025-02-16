package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.Controllers.BaseControllerEstablishment;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.CategoryDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryCreateRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryPatchRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryUpdateRequest;
import com.api.glovoCRM.Services.EstablishmentServices.CategoryService;
import com.api.glovoCRM.mappers.CategoryMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController extends BaseControllerEstablishment<CategoryDTO, Category, CategoryCreateRequest, CategoryUpdateRequest, CategoryPatchRequest> {


    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        super(categoryService, categoryMapper);
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("get-name-dsl")
    public ResponseEntity<List<CategoryDTO>> getByNameDsl(@RequestParam String name) {
        List<Category> categories = categoryService.getCategoriesByNameDSL(name);
        return ResponseEntity.ok(categoryMapper.toDTOList(categories));
    }
}
