package com.api.glovoCRM.DTOs.OrderDTOs;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.OrderDetailModels.PaymentDetail;
import com.api.glovoCRM.constants.EStatusOrder;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class OrderDTO {
    private BigDecimal totalAmount;
    private EStatusOrder status;
    private List<OrderItemDTO> orderItems;
    private Long userId;
    private AddressDTO address;
    private OrderDetailDTO orderDetail;
    private PaymentDetailDTO paymentDetail;
}
