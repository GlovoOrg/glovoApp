package com.api.glovoCRM.mappers.OrderMappers;

import com.api.glovoCRM.DTOs.OrderDTOs.OrderItemDTO;
import com.api.glovoCRM.Models.OrderDetailModels.Order;
import com.api.glovoCRM.Models.OrderDetailModels.OrderItem;
import com.api.glovoCRM.mappers.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper extends BaseMapper<OrderItem, OrderItemDTO> {
    @Override
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    OrderItemDTO toDTO(OrderItem orderItem);
}
