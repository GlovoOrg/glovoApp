package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.EstablishmentDTOs.ProductDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "originalPrice", source = "price")
    @Mapping(target = "finalPrice", source = "product", qualifiedByName = "calculateFinalPrice")
    @Mapping(target = "discountPercentage", source = "product", qualifiedByName = "getDiscountPercentage")
    @Mapping(target = "discountMessage", source = "product", qualifiedByName = "getDiscountMessage")
    ProductDTO toDTO(Product product);

    @Named("calculateFinalPrice")
    default BigDecimal calculateFinalPrice(Product product) {
        if (product.getDiscountProduct() != null && product.getDiscountProduct().isActive()) {
            BigDecimal discount = BigDecimal.valueOf(product.getDiscountProduct().getDiscount());
            return product.getPrice().multiply(BigDecimal.ONE.subtract(discount));
        }
        return product.getPrice();
    }

    @Named("getDiscountPercentage")
    default int getDiscountPercentage(Product product) {
        if (product == null) return 0;
        if (product.getDiscountProduct() != null && product.getDiscountProduct().isActive()) {
            return product.getDiscountProduct().getDiscount() * 100;
        }
        return 0;
    }

    @Named("getDiscountMessage")
    default String getDiscountMessage(Product product) {
        if (product == null) return null;
        if (product.getDiscountProduct() != null && product.getDiscountProduct().isActive()) {
            return String.format("Скидка %d%%", product.getDiscountProduct().getDiscount() * 100);
        }
        return null;
    }
}
