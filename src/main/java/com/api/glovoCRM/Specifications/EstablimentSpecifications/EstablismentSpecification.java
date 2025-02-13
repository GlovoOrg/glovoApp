package com.api.glovoCRM.Specifications.EstablimentSpecifications;

import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Specifications.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EstablismentSpecification extends BaseSpecification<Establishment> {
    public Specification<Establishment> getEstablishmentByRatingAscFilter() {
        return  (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("totalRating")));
            return criteriaBuilder.conjunction();
        };
    }

    public Specification<Establishment> getEstablishmentByRatingDescFilter() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("totalRating")));
            return criteriaBuilder.conjunction();
        };
    }

    public Specification<Establishment> getEstablishmentByDeliveryPriceAscFilter() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("priceOfDelivery")));
            return criteriaBuilder.conjunction();
        };
    }

    public Specification<Establishment> getEstablishmentByDeliveryPriceDescFilter() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("priceOfDelivery")));
            return criteriaBuilder.conjunction();
        };
    }
}
