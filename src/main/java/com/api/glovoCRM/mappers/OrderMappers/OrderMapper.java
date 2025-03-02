package com.api.glovoCRM.mappers.OrderMappers;

import com.api.glovoCRM.DTOs.OrderDTOs.OrderDTO;
import com.api.glovoCRM.Models.OrderDetailModels.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, OrderDetailMapper.class, AddressMapper.class, PaymentDetailMapper.class})
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "orderDetail", target = "orderDetail")
    @Mapping(source = "paymentDetail", target = "paymentDetail") // Добавляем маппинг для PaymentDetail
    @Mapping(source = "orderItems", target = "orderItems")
    OrderDTO toDTO(Order order);
}
