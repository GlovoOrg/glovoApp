package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.UserDTOs.CustomerDTO;
import com.api.glovoCRM.Models.UserModels.User;
import com.stripe.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CustomerMapper extends BaseMapper<User, CustomerDTO> {
    @Override
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phone")
    CustomerDTO toDTO(User user);

    @Override
    default List<CustomerDTO> toDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::toDTO)
                .toList();
    }

}
