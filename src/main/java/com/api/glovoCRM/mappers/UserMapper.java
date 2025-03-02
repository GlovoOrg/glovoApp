package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.UserDTOs.AdminAndEstablishmentDTO;
import com.api.glovoCRM.Models.UserModels.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, AdminAndEstablishmentDTO> {
    @Override
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "lastLoginDate", target = "lastLoginDate")
    @Mapping(source = "login", target = "login")
    @Mapping(source = "roles", target = "roles")
    @Mapping(source = "socialAccounts", target = "socialAccounts")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdTime", target = "createdTime")
    @Mapping(source = "updatedTime", target = "updatedTime")
    AdminAndEstablishmentDTO toDTO(User user);

    @Override
    default List<AdminAndEstablishmentDTO> toDTOList(List<User> users) {
        if(users == null) {
            return null;
        }
        return users.stream()
                .map(this::toDTO)
                .toList();
    }
}
