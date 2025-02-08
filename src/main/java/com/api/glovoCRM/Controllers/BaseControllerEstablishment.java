package com.api.glovoCRM.Controllers;


import com.api.glovoCRM.Rest.Requests.BaseRequest;
import com.api.glovoCRM.Rest.Requests.BaseRequestNotNull;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.mappers.BaseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Tag(name = "Базовый контроллер", description = "Базовый контроллер для управления сущностями")
public abstract class BaseControllerEstablishment<DTO, ENTITY, CREATE_REQUEST extends BaseRequestNotNull, UPDATE_REQUEST extends BaseRequestNotNull, PATCH_REQUEST extends BaseRequest> {

    private final BaseService<ENTITY, CREATE_REQUEST, UPDATE_REQUEST, PATCH_REQUEST> baseService;
    private final BaseMapper<ENTITY, DTO> baseMapper;

    @Autowired
    public BaseControllerEstablishment(BaseService<ENTITY, CREATE_REQUEST, UPDATE_REQUEST, PATCH_REQUEST> baseService, BaseMapper<ENTITY, DTO> baseMapper) {
        this.baseService = baseService;
        this.baseMapper = baseMapper;
    }

    @Operation(summary = "Создать сущность", description = "Добавляет новую сущность")
    @ApiResponse(responseCode = "201", description = "Сущность успешно создана")
    @PostMapping
    public ResponseEntity<DTO> createEntity(@Valid @ModelAttribute CREATE_REQUEST request) {
        ENTITY entity = baseService.createEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(baseMapper.toDTO(entity));
    }

    @Operation(summary = "Получить сущность по ID", description = "Возвращает сущность по указанному ID")
    @ApiResponse(responseCode = "200", description = "Сущность успешно найдена")
    @GetMapping("/{id}")
    public ResponseEntity<DTO> getEntity(@PathVariable Long id) {
        ENTITY entity = baseService.findById(id);
        return ResponseEntity.ok(baseMapper.toDTO(entity));
    }
    @Operation(summary = "Получить сущность по имени")
    @ApiResponse(responseCode = "200", description = "Успешный поиск")
    @GetMapping("/name")
    public ResponseEntity<DTO> getEntityByName(@RequestParam @NotBlank String name){
        ENTITY entity = baseService.findByName(name);
        return ResponseEntity.ok(baseMapper.toDTO(entity));
    }
    @Operation(summary = "Получить все сущности", description = "Возвращает список всех сущностей")
    @ApiResponse(responseCode = "200", description = "Список сущностей успешно получен")
    @GetMapping
    public ResponseEntity<List<DTO>> getAllEntities() {
        List<ENTITY> entities = baseService.findAll();
        return ResponseEntity.ok(baseMapper.toDTOList(entities));
    }

    @Operation(summary = "Обновить сущность", description = "Обновляет сущность по ID")
    @ApiResponse(responseCode = "200", description = "Сущность успешно обновлена")
    @PutMapping("/{id}")
    public ResponseEntity<DTO> updateEntity(@PathVariable Long id, @Valid @ModelAttribute UPDATE_REQUEST request) {
        ENTITY entity = baseService.updateEntity(id, request);
        return ResponseEntity.ok(baseMapper.toDTO(entity));
    }

    @Operation(summary = "Частично обновить сущность", description = "Частично обновляет сущность по ID")
    @ApiResponse(responseCode = "200", description = "Сущность успешно частично обновлена")
    @PatchMapping("/{id}")
    public ResponseEntity<DTO> patchEntity(@PathVariable Long id, @Valid @ModelAttribute PATCH_REQUEST request) {
        ENTITY entity = baseService.patchEntity(id, request);
        return ResponseEntity.ok(baseMapper.toDTO(entity));
    }

    @Operation(summary = "Удалить сущность", description = "Удаляет сущность по ID")
    @ApiResponse(responseCode = "204", description = "Сущность успешно удалена")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntity(@PathVariable Long id) {
        baseService.deleteEntity(id);
        return ResponseEntity.noContent().build();
    }
}