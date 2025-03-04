package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.ProductDTO;
import com.api.glovoCRM.DTOs.UserDTOs.SocialAccountDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Models.UserModels.SocialAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper (componentModel = "spring")
public interface SocialAccountMapper extends BaseMapper<SocialAccount, SocialAccountDTO> {
    @Override
    @Mapping(target = "provider", source = "provider")
    @Mapping(target ="providerId", source = "providerId")
    SocialAccountDTO toDTO(SocialAccount socialAccount);

    @Override
    default List<SocialAccountDTO> toDTOList(List<SocialAccount> productDTOS) {
        return productDTOS.stream()
                .map(this::toDTO)
                .toList();
    }
}
