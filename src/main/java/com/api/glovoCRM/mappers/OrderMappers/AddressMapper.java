package com.api.glovoCRM.mappers.OrderMappers;

import com.api.glovoCRM.DTOs.OrderDTOs.AddressDTO;
import com.api.glovoCRM.Models.OrderDetailModels.Address;
import com.api.glovoCRM.mappers.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper extends BaseMapper<Address, AddressDTO> {
    @Override
    @Mapping(source = "order.id", target = "orderId")
    AddressDTO toDTO(Address address);
}
