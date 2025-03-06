package com.api.glovoCRM.Services.OrderServices;

import com.api.glovoCRM.Repositories.OrderRepository;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.OrderDetailModels.*;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.OrderRequests.OrderAddressRequest;
import com.api.glovoCRM.Services.CartService.CartService;
import com.api.glovoCRM.Services.EstablishmentServices.ProductService;
import com.api.glovoCRM.Services.UserServices.UserService;
import com.api.glovoCRM.Utils.Stripe.PaymentService;
import com.api.glovoCRM.constants.EStatusOrder;
import com.stripe.exception.StripeException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository, CartService cartService, UserService userService, ProductService productService, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.productService = productService;
        this.paymentService = paymentService;
    }

    @Transactional
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

        //todo когда нибудь допилю, но сейчас положа руку на сердцо, не хочу этого делать
        int maxTimeToDelivery = 0;
        double distanceToDelivery = 10; // рандомное значение
        double costOfDelivery = 0;



        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cart.getItems()) {
            Establishment establishment = productService.findById(cartItem.getProductId()).getEstablishment();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOneProductPriceOrder(cartItem.getOneProductPriceCart());
            orderItem.setProduct(productService.findById(cartItem.getProductId()));
            orderItems.add(orderItem);
            if (establishment.getTimeOfDelivery() > maxTimeToDelivery) {
                maxTimeToDelivery = establishment.getTimeOfDelivery();
            }
            if(establishment.getPriceOfDelivery() > costOfDelivery) {
                costOfDelivery = establishment.getPriceOfDelivery();
            }
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
        orderDetail.setTimeOfDelivery(maxTimeToDelivery);
        orderDetail.setCostOfDelivery(BigDecimal.valueOf(costOfDelivery));
        orderDetail.setDistance(distanceToDelivery);
        order.setOrderDetail(orderDetail);

        BigDecimal totalAmountWithDelivery = cart.getTotalCharge()
                .add(BigDecimal.valueOf(costOfDelivery));
        order.setTotalAmount(totalAmountWithDelivery);

        PaymentDetail paymentDetail;
        try {
            paymentDetail = paymentService.createCheckoutSession(order);
            order.setPaymentDetail(paymentDetail);
        } catch (StripeException ex) {
            throw new RuntimeException(ex);
        }


        Order savedOrder = orderRepository.save(order);

        cartService.clearCartByUserId(userId);

        return savedOrder;


    }
}
