package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs.CategoryDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryCreateRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryPatchRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryUpdateRequest;
import com.api.glovoCRM.Services.EstablishmentServices.CategoryService;
import com.api.glovoCRM.mappers.CategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Validated
@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "API для управления категориями")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }
    @Operation(summary = "Создать категорию", description = "Добавляет новую категорию с изображением")
    @ApiResponse(responseCode = "201", description = "Категория успешно создана")
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(
            @Valid @ModelAttribute CategoryCreateRequest request
    ) {
        Category category = categoryService.createEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toDTO(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        List<CategoryDTO> dtos = categories.stream()
                .map(categoryMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute CategoryUpdateRequest request
    ) {
        Category category = categoryService.updateEntity(id, request);
        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDTO> patchCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute CategoryPatchRequest request
    ) {
        Category category = categoryService.patchEntity(id, request);
        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteEntity(id);
        return ResponseEntity.noContent().build();
    }
}
