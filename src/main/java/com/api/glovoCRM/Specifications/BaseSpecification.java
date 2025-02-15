package com.api.glovoCRM.Specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseSpecification <T>{
    public Specification<T> getBySimilarNameFilter(String name){
        return (root, query, criteriaBuilder) -> {
            if(name == null || name.isEmpty()){
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + name.toLowerCase() + "%";

            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                    pattern
            );
        };
    }


}
