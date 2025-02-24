package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.Controllers.BaseControllerEstablishment;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.SubCategoryDTO;
import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryCreateRequest;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryPatchRequest;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryUpdateRequest;
import com.api.glovoCRM.Services.EstablishmentServices.SubCategoryService;
import com.api.glovoCRM.mappers.SubCategoryMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subcategories")
public class SubCategoryController extends BaseControllerEstablishment<
        SubCategoryDTO,
        SubCategory,
        SubCategoryCreateRequest,
        SubCategoryUpdateRequest,
        SubCategoryPatchRequest> {

    private final SubCategoryService subCategoryService;
    private final SubCategoryMapper subCategoryMapper;

    public SubCategoryController( SubCategoryMapper subCategoryMapper, SubCategoryService subCategoryService) {
        super(subCategoryService, subCategoryMapper);
        this.subCategoryService = subCategoryService;
        this.subCategoryMapper = subCategoryMapper;
    }


}
