package com.api.glovoCRM.mappers;

import com.api.glovoCRM.DTOs.EstablishmentDTOs.ProductDTO;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = {DiscountProductMapper.class, EstablishmentFilterMapper.class})
public interface ProductMapper extends BaseMapper<ProductDTO, Product> {

    @Mapping(target = "description", source = "description")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "imageUrl", expression = "java(com.api.glovoCRM.DTOs.EstablishmentDTOs.Utils.ImageUtil.getImageUrl(product.getId(), com.api.glovoCRM.constants.EntityType.Product))")
    @Mapping(target = "originalPrice", source = "price")
    @Mapping(target = "finalPrice", expression = "java(calculateFinalPrice(product))")
    @Mapping(target = "discountPercentage", expression = "java(getDiscountPercentage(product))")
    @Mapping(target = "discountMessage", expression = "java(getDiscountMessage(product))")
    @Mapping(target = "discountProductId", source = "discountProductId")
    @Mapping(target = "establishmentId", source = "establishmentId")
    @Mapping(target = "establishmentFilterId", source = "establishmentFilterId")
    ProductDTO toDTO(Product product);

    default BigDecimal calculateFinalPrice(Product product) {
        if (product.getDiscountProduct() != null && product.getDiscountProduct().isActive()) {
            BigDecimal discount = BigDecimal.valueOf(product.getDiscountProduct().getDiscount());
            return product.getPrice().multiply(BigDecimal.ONE.subtract(discount));
        }
        return product.getPrice();
    }

    default int getDiscountPercentage(Product product) {
        if (product.getDiscountProduct() != null && product.getDiscountProduct().isActive()) {
            return product.getDiscountProduct().getDiscount() * 100;
        }
        return 0;
    }

    default String getDiscountMessage(Product product) {
        if (product.getDiscountProduct() != null && product.getDiscountProduct().isActive()) {
            return String.format("Скидка %d%%", product.getDiscountProduct().getDiscount() * 100);
        }
        return null;
    }

    @Override
    default List<Product> toDTOList(List<ProductDTO> productDTOS) {
        return productDTOS.stream()
                .map(this::toDTO)
                .toList();
    }
}
