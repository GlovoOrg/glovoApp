package com.api.glovoCRM.Services.UserServices;

import com.api.glovoCRM.DAOs.OrderDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.OrderDetailModels.*;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.OrderRequests.OrderAddressRequest;
import com.api.glovoCRM.Services.CartService.CartService;
import com.api.glovoCRM.Services.EstablishmentServices.ProductService;
import com.api.glovoCRM.constants.EStatusOrder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderDAO orderDAO;
    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    public OrderService(OrderDAO orderDAO, CartService cartService, UserService userService, ProductService productService) {
        this.orderDAO = orderDAO;
        this.cartService = cartService;
        this.userService = userService;
        this.productService = productService;
    }

    public Order createOrderFromCart(Long userId, OrderAddressRequest addressRequest) {
        Optional<Cart> cartOptional = cartService.getCartByUserId(userId);
        if(cartOptional.isEmpty() || cartOptional.get().getItems().isEmpty()) {
            throw new SuchResourceNotFoundEx("Cart is empty or not exist");
        }
        Cart cart = cartOptional.get();

        User user = userService.findById(userId);
        if(user == null) {
            throw new SuchResourceNotFoundEx("User not found");
        }

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(cart.getTotalCharge());
        order.setStatus(EStatusOrder.ORDER_STATUS_CREATED);

        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOneProductPriceOrder(cartItem.getOneProductPriceCart());
            orderItem.setProduct(productService.findById(cartItem.getProductId()));
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        Address address = new Address();
        address.setAddressLine(addressRequest.getAddressLine());
        address.setLatitude(addressRequest.getLatitude());
        address.setLongitude(addressRequest.getLongitude());
        address.setOrder(order);
        order.setAddress(address);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setTimeOfDelivery(1);
        orderDetail.setCostOfDelivery(0);
        orderDetail.setDistance(0.0);
        order.setOrderDetail(orderDetail);

        Order savedOrder = orderDAO.save(order);

        cartService.clearCartByUserId(userId);

        return savedOrder;


    }
}
