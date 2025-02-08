package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.Controllers.BaseControllerEstablishment;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentShortDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.SubCategoryDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentCreateRequest;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentPatchRequest;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentUpdateRequest;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.Services.EstablishmentServices.EstablishmentService;
import com.api.glovoCRM.mappers.BaseMapper;
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
public class EstablishmentController extends BaseControllerEstablishment<EstablishmentDTO, Establishment, EstablishmentCreateRequest, EstablishmentUpdateRequest, EstablishmentPatchRequest> {

    public EstablishmentController(EstablishmentService establishmentService, EstablishmentMapper mapper) {
        super(establishmentService, mapper);
    }
}
