package com.api.glovoCRM.Controllers.Establishment;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.ProductDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Services.EstablishmentServices.SearchService;
import com.api.glovoCRM.mappers.EstablishmentMapper;
import com.api.glovoCRM.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchService searchService;
    private final EstablishmentMapper establishmentMapper;
    private final ProductMapper productMapper;

    @Autowired
    public SearchController(SearchService searchService, EstablishmentMapper establishmentMapper, ProductMapper productMapper) {
        this.searchService = searchService;
        this.establishmentMapper = establishmentMapper;
        this.productMapper = productMapper;
    }

    @GetMapping("/name")
    public ResponseEntity<List<List<?>>> searchByName(@RequestParam String name) {
        List<List<?>> entityEstablishmentAndProduct = searchService.searchEstablishmentAndProductByNameFilter(name);
        List<Establishment> establishments = (List<Establishment>) entityEstablishmentAndProduct.get(0);
        List<Product> products = (List<Product>) entityEstablishmentAndProduct.get(1);
        List<EstablishmentDTO> establishmentDTOS = establishmentMapper.toDTOList(establishments);
        List<ProductDTO> productDTOS = productMapper.toDTOList(products);

        List<List<?>> result = new ArrayList<>();
        result.add(establishmentDTOS);
        result.add(productDTOS);
        return ResponseEntity.ok(result);
    }
}
