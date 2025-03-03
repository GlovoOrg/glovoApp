package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.Repositories.EstablishmentRepository;
import com.api.glovoCRM.Repositories.ProductRepository;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Specifications.EstablimentSpecifications.SearchSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private final EstablishmentRepository establishmentRepository;
    private final ProductRepository productRepository;
    private final SearchSpecification searchSpecification;


    @Autowired
    public SearchService(EstablishmentRepository establishmentRepository, ProductRepository productRepository, SearchSpecification searchSpecification) {
        this.establishmentRepository = establishmentRepository;
        this.productRepository = productRepository;
        this.searchSpecification = searchSpecification;
    }

    public List<List<?>> searchEstablishmentAndProductByNameFilter(String name) {
        Specification<Establishment> establishmentSpecification = searchSpecification.getEstablishmentsFilter(name);
        Specification<Product> productSpecification = searchSpecification.getProductsFilter(name);

        List<Establishment> establishments = establishmentRepository.findAll(establishmentSpecification);
        List<Product> products = productRepository.findAll(productSpecification);

        List<List<?>> establishmentsAndProducts = new ArrayList<>();
        establishmentsAndProducts.add(establishments);
        establishmentsAndProducts.add(products);

        return establishmentsAndProducts;
    }


}
