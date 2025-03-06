package com.api.glovoCRM.Controllers.Customer.UserControllers;

import com.api.glovoCRM.DTOs.OrderDTOs.OrderDTO;
import com.api.glovoCRM.DTOs.UserDTOs.CustomerDTO;
import com.api.glovoCRM.Models.OrderDetailModels.Order;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.UserRequests.CustomerPatchRequest;
import com.api.glovoCRM.Services.UserServices.CustomerService;
import com.api.glovoCRM.mappers.CustomerMapper;
import com.api.glovoCRM.mappers.OrderMappers.OrderMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {
    private final CustomerService customerService;
    private final OrderMapper orderMapper;
    private final CustomerMapper customerMapper;

    public CustomerController(CustomerService customerService, OrderMapper orderMapper, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.orderMapper = orderMapper;
        this.customerMapper = customerMapper;
    }
    @PreAuthorize ("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = customerService.getAllOrders();
        return ResponseEntity.ok(orderMapper.toDTOList(orders));
    }
    @PreAuthorize ("hasRole('ROLE_CUSTOMER')")
    @PatchMapping("/profile")
    public ResponseEntity<CustomerDTO> patchCustomer(@RequestBody CustomerPatchRequest request) {
        User updatedUser = customerService.patchCustomer(request);
        return ResponseEntity.ok(customerMapper.toDTO(updatedUser));
    }
    @PreAuthorize ("hasRole('ROLE_CUSTOMER')")
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteUserProfile() {
        customerService.deleteUserProfile();
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize ("hasRole('ROLE_CUSTOMER')")
    @GetMapping
    public ResponseEntity<?> getCustomerProfile(){
        User user = customerService.getUserProfileFromContext();
        return ResponseEntity.ok(customerMapper.toDTO(user));
    }
}
