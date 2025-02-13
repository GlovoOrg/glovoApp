package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.EstablishmentDAO;
import com.api.glovoCRM.DAOs.ProductDAO;
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
    private final EstablishmentDAO establishmentDao;
    private final ProductDAO productDao;
    private final SearchSpecification searchSpecification;


    @Autowired
    public SearchService(EstablishmentDAO establishmentDao, ProductDAO productDao, SearchSpecification searchSpecification) {
        this.establishmentDao = establishmentDao;
        this.productDao = productDao;
        this.searchSpecification = searchSpecification;
    }

    public List<List<?>> searchEstablishmentAndProductByNameFilter(String name) {
        Specification<Establishment> establishmentSpecification = searchSpecification.getEstablishmentsFilter(name);
        Specification<Product> productSpecification = searchSpecification.getProductsFilter(name);

        List<Establishment> establishments = establishmentDao.findAll(establishmentSpecification);
        List<Product> products = productDao.findAll(productSpecification);

        List<List<?>> establishmentsAndProducts = new ArrayList<>();
        establishmentsAndProducts.add(establishments);
        establishmentsAndProducts.add(products);

        return establishmentsAndProducts;
    }
}
