package com.api.glovoCRM.Controllers.Customer.UserControllers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.ProductDTO;
import com.api.glovoCRM.DTOs.UserDTOs.CustomerDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindProductFilterRequest;
import com.api.glovoCRM.Services.UserServices.UserEstablishmentService;
import com.api.glovoCRM.mappers.CustomerMapper;
import com.api.glovoCRM.mappers.ProductMapper;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-establishment")
public class UserEstablishment {
    private final UserEstablishmentService userEstablishmentService;
    private final CustomerMapper customerMapper;
    private final ProductMapper productMapper;

    public UserEstablishment(UserEstablishmentService userEstablishmentService, CustomerMapper customerMapper, ProductMapper productMapper) {
        this.userEstablishmentService = userEstablishmentService;
        this.customerMapper = customerMapper;
        this.productMapper = productMapper;
    }

    @GetMapping("/{establishmentId}/customers")
    public ResponseEntity<List<CustomerDTO>> getCustomersByEstablishment(@PathVariable Long establishmentId) {
        List<User> users = userEstablishmentService.getCustomersByEstablishmentId(establishmentId);
        return ResponseEntity.ok(customerMapper.toDTOList(users));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getProductsByEstablishmentByFilter(AdminFindProductFilterRequest request) {
        List<Product> products = userEstablishmentService.getProductsByFilter(request);
        return ResponseEntity.ok(productMapper.toDTOList(products));
    }

}
