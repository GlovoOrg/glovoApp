package com.api.glovoCRM.Controllers.Admin;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTO;
import com.api.glovoCRM.DTOs.EstablishmentDTOs.ProductDTO;
import com.api.glovoCRM.DTOs.UserDTOs.AdminAndEstablishmentDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindEstablishmentFilterRequest;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindProductFilterRequest;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindUserFilterRequest;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminPatchRequest;
import com.api.glovoCRM.Services.UserServices.AdminService;
import com.api.glovoCRM.mappers.EstablishmentMapper;
import com.api.glovoCRM.mappers.ProductMapper;
import com.api.glovoCRM.mappers.UserMapper;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;
    private final UserMapper userMapper;
    private final EstablishmentMapper establishmentMapper;
    private final ProductMapper productMapper;

    @Autowired
    public AdminController(AdminService adminService, UserMapper userMapper, EstablishmentMapper establishmentMapper, ProductMapper productMapper) {
        this.adminService = adminService;
        this.userMapper = userMapper;
        this.establishmentMapper = establishmentMapper;
        this.productMapper = productMapper;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminAndEstablishmentDTO>> getUsersByFilter(@RequestBody AdminFindUserFilterRequest filter) {
        List<User> users = adminService.getUsersByFilter(filter);
        return ResponseEntity.ok(userMapper.toDTOList(users));
    }

    @GetMapping("/establishments")
    public ResponseEntity<List<EstablishmentDTO>> getEstablishmentsByFilter(@RequestBody AdminFindEstablishmentFilterRequest filter) {
        List<Establishment> establishments = adminService.getEstablishmentsByFilter(filter);
        return ResponseEntity.ok(establishmentMapper.toDTOList(establishments));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getProductsByFilter(@RequestBody AdminFindProductFilterRequest filter) {
        List<Product> products = adminService.getProductsByFilter(filter);
        return ResponseEntity.ok(productMapper.toDTOList(products));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<AdminAndEstablishmentDTO> patchUser(@PathVariable Long id, @RequestBody AdminPatchRequest request) {
        User updatedUser = adminService.patchUser(id, request);
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }


}
