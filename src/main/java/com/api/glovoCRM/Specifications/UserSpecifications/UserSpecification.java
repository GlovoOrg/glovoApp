package com.api.glovoCRM.Specifications.UserSpecifications;

import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Models.OrderDetailModels.Order;
import com.api.glovoCRM.Models.OrderDetailModels.OrderItem;
import com.api.glovoCRM.Models.UserModels.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification {

    public Specification<User> getCustomersByEstablishmentId(Long establishmentId) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            Join<User, Order> orderJoin = root.join("orderEntities", JoinType.INNER);

            Join<Order, OrderItem> orderItemJoin = orderJoin.join("orderItems", JoinType.INNER);

            Join<OrderItem, Product> productJoin = orderItemJoin.join("product", JoinType.INNER);

            return criteriaBuilder.equal(productJoin.get("establishment").get("id"), establishmentId);
        };
    }
}
