package com.api.glovoCRM.Specifications.UserSpecifications;

import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindEstablishmentFilterRequest;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindProductFilterRequest;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindUserFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class AdminSpecification {

    public Specification<User> getUserByFilter(AdminFindUserFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getUsername() != null && !filter.getUsername().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.getUsername().toLowerCase() + "%"));
            }

            if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + filter.getEmail().toLowerCase() + "%"));
            }

            if (filter.getPhoneNumber() != null && !filter.getPhoneNumber().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("phoneNumber"),
                        "%" + filter.getPhoneNumber() + "%"));
            }

            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"), filter.getStatus()));
            }

            if (filter.getIsStaff() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("isStaff"), filter.getIsStaff()));
            }

            if (filter.getChatId() != null && !filter.getChatId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("chatId"), filter.getChatId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
    public Specification<Establishment> getEstablishmentsByFilter(AdminFindEstablishmentFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filter.getId()));
            }

            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"));
            }

            if (filter.getPriceOfDelivery() > 0) {
                predicates.add(criteriaBuilder.equal(root.get("priceOfDelivery"), filter.getPriceOfDelivery()));
            }

            if (filter.getTotalRating() > 0) {
                predicates.add(criteriaBuilder.equal(root.get("totalRating"), filter.getTotalRating()));
            }

            if (filter.getOpenTime() != null) {
                predicates.add(criteriaBuilder.equal(root.get("openTime"), filter.getOpenTime()));
            }

            if (filter.getCloseTime() != null) {
                predicates.add(criteriaBuilder.equal(root.get("closeTime"), filter.getCloseTime()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public Specification<Product> getProductsByFilter(AdminFindProductFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"));
            }

            if (filter.getDescription() != null && !filter.getDescription().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + filter.getDescription().toLowerCase() + "%"));
            }

            if (filter.getPrice() != null && filter.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(criteriaBuilder.equal(root.get("price"), filter.getPrice()));
            }

            if (filter.getActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), filter.getActive()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
