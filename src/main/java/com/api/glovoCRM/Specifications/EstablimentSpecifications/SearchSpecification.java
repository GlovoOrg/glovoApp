package com.api.glovoCRM.Specifications.EstablimentSpecifications;

import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Specifications.BaseSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SearchSpecification{
    private final EstablismentSpecification establismentSpecification;
    private final ProductSpecification productSpecification;

    @Autowired
    public SearchSpecification(EstablismentSpecification establismentSpecification, ProductSpecification productSpecification) {
        this.establismentSpecification = establismentSpecification;
        this.productSpecification = productSpecification;
    }

    public Specification<Establishment> getEstablishmentsFilter(String name) {
        return establismentSpecification.getBySimilarNameFilter(name);
    }

    public Specification<Product> getProductsFilter(String name) {
        return productSpecification.getBySimilarNameFilter(name);
    }
}
