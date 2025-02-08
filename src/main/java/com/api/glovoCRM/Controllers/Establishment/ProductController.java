package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.ProductDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Rest.Requests.ProductCreateRequest;
import com.api.glovoCRM.Rest.Requests.ProductPatchRequest;
import com.api.glovoCRM.Rest.Requests.ProductUpdateRequest;
import com.api.glovoCRM.Services.EstablishmentServices.ProductService;

import com.api.glovoCRM.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

}
