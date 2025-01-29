package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.CategoryDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Rest.Requests.CategoryPatchRequest;
import com.api.glovoCRM.Services.EstablishmentServices.CategoryService;
import com.api.glovoCRM.mappers.CategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Tag(name = "category_method")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;


    @Operation(
            summary = "Создать новую категорию.",
            description = "Создаёт новую категорию с указанным именем и изображением."
    )
    @ApiResponse(responseCode = "201", description = "Категория успешно создана.")
    @ApiResponse(responseCode = "400", description = "Некорректные входные данные.")

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(
            @RequestParam String name,
            @RequestParam MultipartFile image
    ) {
        Category category = categoryService.createCategory(name, image);
        CategoryDTO dto = categoryMapper.toDTO(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    @Operation(
            summary = "Получить категорию по ID",
            description = "Возвращает данные категории по указанному идентификатору."
    )
    @ApiResponse(responseCode = "200", description = "Категория успешно найдена.")
    @ApiResponse(responseCode = "404", description = "Категория с указанным ID не найдена.")

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }
    @Operation(
            summary = "Получить все категории",
            description = "Возвращает список всех доступных категорий."
    )
    @ApiResponse(responseCode = "200", description = "Список категорий успешно получен.")

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> dtos = categories.stream()
                .map(categoryMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }
    @Operation(
            summary = "Обновить категорию",
            description = "Обновляет данные категории по указанному ID. Можно изменить имя категории и/или изображение."
    )
    @ApiResponse(responseCode = "200", description = "Категория успешно обновлена.")
    @ApiResponse(responseCode = "404", description = "Категория с указанным ID не найдена.")

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) MultipartFile image
    ) {
        Category category = categoryService.updateCategory(id, name, image);
        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }
    @Operation(
            summary = "Частично обновить категорию",
            description = "Обновляет отдельные данные категории по её ID."
    )
    @ApiResponse(responseCode = "200", description = "Категория успешно обновлена.")
    @ApiResponse(responseCode = "404", description = "Категория с указанным ID не найдена.")

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDTO> patchCategory(
            @PathVariable Long id,
            @ModelAttribute CategoryPatchRequest request
    ) {
        Category category = categoryService.patchCategory(id, request);
        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }
    @Operation(
            summary = "Удалить категорию",
            description = "Удаляет категорию по указанному идентификатору."
    )
    @ApiResponse(responseCode = "204", description = "Категория успешно удалена.")
    @ApiResponse(responseCode = "404", description = "Категория с указанным ID не найдена.")

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
