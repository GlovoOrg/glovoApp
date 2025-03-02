package com.api.glovoCRM.mappers.OrderMappers;

import com.api.glovoCRM.DTOs.OrderDTOs.OrderDetailDTO;
import com.api.glovoCRM.Models.OrderDetailModels.OrderDetail;
import com.api.glovoCRM.mappers.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper extends BaseMapper<OrderDetail, OrderDetailDTO> {
    @Override
    @Mapping(source = "order.id", target = "orderId")
    OrderDetailDTO toDTO(OrderDetail orderDetail);
}
