package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.Controllers.BaseControllerEstablishment;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentCreateRequest;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentPatchRequest;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentUpdateRequest;
import com.api.glovoCRM.Services.EstablishmentServices.EstablishmentService;
import com.api.glovoCRM.mappers.EstablishmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/establishments")
public class EstablishmentController extends BaseControllerEstablishment<EstablishmentDTO, Establishment, EstablishmentCreateRequest, EstablishmentUpdateRequest, EstablishmentPatchRequest> {

    private final EstablishmentService establishmentService;
    private final EstablishmentMapper establishmentMapper;

    public EstablishmentController(EstablishmentService establishmentService, EstablishmentMapper mapper, EstablishmentService establishmentService1, EstablishmentMapper establishmentMapper) {
        super(establishmentService, mapper);
        this.establishmentService = establishmentService1;
        this.establishmentMapper = establishmentMapper;
    }

    @GetMapping("/total-rating-asc")
    public ResponseEntity<List<EstablishmentDTO>> getEstablishmentByTotalRatingAscFilter() {
        List<Establishment> establishments = establishmentService.getEstablishmentsByRatingAscFilter();
        return ResponseEntity.ok(establishmentMapper.toDTOList(establishments));
    }

    @GetMapping("/total-rating-desc")
    public ResponseEntity<List<EstablishmentDTO>> getEstablishmentByTotalRatingDescFilter() {
        List<Establishment> establishments = establishmentService.getEstablishmentsByRatingDescFilter();
        return ResponseEntity.ok(establishmentMapper.toDTOList(establishments));
    }

    @GetMapping("/delivery-price-asc")
    public ResponseEntity<List<EstablishmentDTO>> getEstablishmentByDeliveryPriceAscFilter() {
        List<Establishment> establishments = establishmentService.getEstablishmentsByDeliveryPriceAscFilter();
        return ResponseEntity.ok(establishmentMapper.toDTOList(establishments));
    }

    @GetMapping("/delivery-price-desc")
    public ResponseEntity<List<EstablishmentDTO>> getEstablishmentByDeliveryPriceDescFilter() {
        List<Establishment> establishments = establishmentService.getEstablishmentsByDeliveryPriceDescFilter();
        return ResponseEntity.ok(establishmentMapper.toDTOList(establishments));
    }
}
