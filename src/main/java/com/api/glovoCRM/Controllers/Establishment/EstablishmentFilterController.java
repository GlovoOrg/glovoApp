package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentFilterDTO;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.EstablishmentFilter;
import com.api.glovoCRM.Rest.Requests.EstablishmentFilterRequests.*;
import com.api.glovoCRM.Services.EstablishmentServices.EstablishmentFilterService;
import com.api.glovoCRM.mappers.EstablishmentFilterMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/establishment-filters")
@Slf4j
public class EstablishmentFilterController {

    private final EstablishmentFilterService establishmentFilterService;
    private final EstablishmentFilterMapper establishmentFilterMapper;

    @Autowired
    public EstablishmentFilterController(EstablishmentFilterService establishmentFilterService, EstablishmentFilterMapper establishmentFilterMapper) {
        this.establishmentFilterService = establishmentFilterService;
        this.establishmentFilterMapper = establishmentFilterMapper;
    }

    @PostMapping
    public ResponseEntity<EstablishmentFilterDTO> createEstablishmentFilter(@Valid @RequestBody EstablishmentFilterCreateRequest request) {
        try {
            EstablishmentFilter createdFilter = establishmentFilterService.createEntity(request);
            log.info("Фильтр успешно создан: {}", createdFilter.getId());
            EstablishmentFilterDTO dto = establishmentFilterMapper.toDTO(createdFilter);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (AlreadyExistsEx | SuchResourceNotFoundEx e) {
            log.warn("Ошибка при создании фильтра: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании фильтра: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<EstablishmentFilter>> getAllEstablishmentFilters() {
        List<EstablishmentFilter> filters = establishmentFilterService.getAll();
        if (filters.isEmpty()) {
            log.info("Список фильтров пуст");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.info("Получено {} фильтров", filters.size());
        return new ResponseEntity<>(filters, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstablishmentFilterDTO> getEstablishmentFilterById(@PathVariable Long id) {
        try {
            EstablishmentFilter filter = establishmentFilterService.findById(id);
            EstablishmentFilterDTO dto = establishmentFilterMapper.toDTO(filter);
            log.info("Фильтр с ID {} успешно получен", id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (SuchResourceNotFoundEx e) {
            log.warn("Фильтр с ID {} не найден", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении фильтра с ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstablishmentFilterDTO> updateEstablishmentFilter(@PathVariable Long id, @Valid @RequestBody EstablishmentFilterUpdateRequest request) {
        try {
            EstablishmentFilter updatedFilter = establishmentFilterService.updateEntity(id, request);
            EstablishmentFilterDTO dto = establishmentFilterMapper.toDTO(updatedFilter);
            log.info("Фильтр с ID {} успешно обновлен", id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (SuchResourceNotFoundEx | AlreadyExistsEx e) {
            log.warn("Ошибка при обновлении фильтра с ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении фильтра с ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EstablishmentFilterDTO> patchEstablishmentFilter(@PathVariable Long id, @RequestBody EstablishmentFilterPatchRequest request) {
        try {
            EstablishmentFilter patchedFilter = establishmentFilterService.patchEntity(id, request);
            EstablishmentFilterDTO dto = establishmentFilterMapper.toDTO(patchedFilter);
            log.info("Фильтр с ID {} успешно частично обновлен", id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (SuchResourceNotFoundEx e) {
            log.warn("Ошибка при частичном обновлении фильтра с ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при частичном обновлении фильтра с ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstablishmentFilter(@PathVariable Long id) {
        try {
            establishmentFilterService.deleteEntity(id);
            log.info("Фильтр с ID {} успешно удален", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (SuchResourceNotFoundEx e) {
            log.warn("Фильтр с ID {} не найден", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении фильтра с ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}