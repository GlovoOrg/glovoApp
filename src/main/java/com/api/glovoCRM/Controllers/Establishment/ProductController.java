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


    private final ProductService productService;
    private final ProductMapper productMapper;

    @Autowired
    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct( @ModelAttribute ProductCreateRequest request) {
        Product createdProduct = productService.createProductWithRelations(request);
        ProductDTO dto = productMapper.toDTO(createdProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        ProductDTO dto = productMapper.toDTO(product);
        return ResponseEntity.ok(dto);
    }
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> dtos = products.stream()
                .map(productMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }
    @PutMapping("/{id}") // полное обновление put
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductUpdateRequest request
    ) {
        Product updatedProduct = productService.updateProduct(id, request);
        ProductDTO dto = productMapper.toDTO(updatedProduct);
        return ResponseEntity.ok(dto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> patchProduct(
            @PathVariable Long id,
            @ModelAttribute ProductPatchRequest request
    ) {
        Product patchedProduct = productService.patchProduct(id, request);
        ProductDTO dto = productMapper.toDTO(patchedProduct);
        return ResponseEntity.ok(dto);
    }
}
