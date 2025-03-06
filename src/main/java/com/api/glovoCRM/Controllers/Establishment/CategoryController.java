package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.Controllers.BaseControllerEstablishment;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.CategoryDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.CategoryWithSubcategoriesAndEstablishmentsDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.CategoryWithSubcategoriesDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryCreateRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryPatchRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryUpdateRequest;
import com.api.glovoCRM.Services.EstablishmentServices.CategoryService;
import com.api.glovoCRM.mappers.CategoryMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

@RestController
@RequestMapping("/api/v1/non-secured/categories")
public class CategoryController extends BaseControllerEstablishment<CategoryDTO, Category, CategoryCreateRequest, CategoryUpdateRequest, CategoryPatchRequest> {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryMapper categoryMapper, CategoryService categoryService) {
        super(categoryService, categoryMapper);
        this.categoryMapper = categoryMapper;
        this.categoryService = categoryService;
    }

    @GetMapping("/categories-with-subcategories")
    public ResponseEntity<List<CategoryWithSubcategoriesDTO>> getCategoriesWithSubcategories() {
        List<Category> categories = categoryService.getCategoriesWithSubcategoriesOnly();
        List<CategoryWithSubcategoriesDTO> result = categories.stream()
                .map(categoryMapper::toCategoryWithSubcategories)
                .toList();
        return ResponseEntity.ok(result);
    }


    @GetMapping("/categories-with-subcategories-and-establishments")
    public ResponseEntity<List<CategoryWithSubcategoriesAndEstablishmentsDTO>> getCategoriesWithSubcategoriesAndEstablishments() {
        List<Category> categories = categoryService.getCategoriesWithSubcategoriesAndEstablishmentsOnly();
        List<CategoryWithSubcategoriesAndEstablishmentsDTO> result = categories.stream()
                .map(categoryMapper::toCategoryWithSubcategoriesAndEstablishments)
                .toList();
        return ResponseEntity.ok(result);
    }
}
