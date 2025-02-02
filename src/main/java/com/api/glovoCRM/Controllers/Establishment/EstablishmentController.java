package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs.EstablishmentDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs.EstablishmentShortDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Services.EstablishmentServices.EstablishmentService;
import com.api.glovoCRM.mappers.EstablishmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/v1/establishments")
public class EstablishmentController {

    private final EstablishmentService establishmentService;
    private final EstablishmentMapper establishmentMapper;

    @Autowired
    public EstablishmentController(EstablishmentService establishmentService, EstablishmentMapper establishmentMapper){
        this.establishmentService = establishmentService;
        this.establishmentMapper = establishmentMapper;
    }
    @GetMapping
    public ResponseEntity<List<EstablishmentShortDTO>> getAllEstablishments() {
        log.info("Получен запрос для всех заведений");
        List<Establishment> establishments = establishmentService.findAll();
        List<EstablishmentShortDTO> dtos = establishments.stream()
                .map(establishmentMapper::toShortDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<EstablishmentDTO> getEstablishmentById(@PathVariable Long id) {
        log.info("Получен запрос для заведения с id: {}", id);
        Establishment establishment = establishmentService.findById(id);
        EstablishmentDTO dto = establishmentMapper.toDTO(establishment);
        return ResponseEntity.ok(dto);
    }
}
