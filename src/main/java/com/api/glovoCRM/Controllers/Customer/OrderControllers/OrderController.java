package com.api.glovoCRM.Controllers.Customer.OrderControllers;

import com.api.glovoCRM.Models.OrderDetailModels.Order;
import com.api.glovoCRM.Rest.Requests.OrderRequests.OrderAddressRequest;
import com.api.glovoCRM.Services.OrderServices.OrderService;
import com.api.glovoCRM.mappers.OrderMappers.OrderMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;



    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }



    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam Long userId, @RequestBody OrderAddressRequest orderAddressRequest) {
        Order order = orderService.createOrderFromCart(userId, orderAddressRequest);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }
}
