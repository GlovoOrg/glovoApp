package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.CategoryDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Rest.Requests.CategoryPatchRequest;
import com.api.glovoCRM.Services.EstablishmentServices.CategoryService;
import com.api.glovoCRM.mappers.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping // checked by Alinur
    public ResponseEntity<CategoryDTO> createCategory(
            @RequestParam String name,
            @RequestParam MultipartFile image
    ) {
        Category category = categoryService.createCategory(name, image);
        CategoryDTO dto = categoryMapper.toDTO(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}") // checked by Alinur
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }

    @GetMapping // checked by Alinur
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> dtos = categories.stream()
                .map(categoryMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}") // checked by Alinur
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) MultipartFile image
    ) {
        Category category = categoryService.updateCategory(id, name, image);
        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }

    @PatchMapping("/{id}") // checked by Alinur
    public ResponseEntity<CategoryDTO> patchCategory(
            @PathVariable Long id,
            @ModelAttribute CategoryPatchRequest request
    ) {
        Category category = categoryService.patchCategory(id, request);
        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }

    @DeleteMapping("/{id}") // checked by Alinur
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    //test
}
