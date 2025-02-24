package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.Controllers.BaseControllerEstablishment;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.ProductDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Rest.Requests.ProductRequests.ProductWithDiscountCreateRequest;
import com.api.glovoCRM.Rest.Requests.ProductRequests.ProductWithDiscountPatchRequest;
import com.api.glovoCRM.Rest.Requests.ProductRequests.ProductWithDiscountUpdateRequest;
import com.api.glovoCRM.Services.EstablishmentServices.ProductService;
import com.api.glovoCRM.mappers.ProductMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController extends BaseControllerEstablishment<ProductDTO, Product, ProductWithDiscountCreateRequest, ProductWithDiscountUpdateRequest, ProductWithDiscountPatchRequest> {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        super(productService, productMapper);
        this.productService = productService;
        this.productMapper = productMapper;
    }


}
