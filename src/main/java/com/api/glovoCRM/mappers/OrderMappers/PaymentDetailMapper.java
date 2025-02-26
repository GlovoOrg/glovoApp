package com.api.glovoCRM.mappers.OrderMappers;

import com.api.glovoCRM.DTOs.OrderDTOs.PaymentDetailDTO;
import com.api.glovoCRM.Models.OrderDetailModels.OrderDetail;
import com.api.glovoCRM.Models.OrderDetailModels.PaymentDetail;
import com.api.glovoCRM.mappers.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentDetailMapper extends BaseMapper<PaymentDetail, PaymentDetailDTO> {
    @Mapping(source = "order.id", target = "orderId")
    PaymentDetailDTO toDTO(PaymentDetail paymentDetail);
}
