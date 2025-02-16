package com.api.glovoCRM.DAOs.QueryDSL.EstablishmentQueryDSL;

import com.api.glovoCRM.DAOs.EstablishmentDAO;
import com.api.glovoCRM.DAOs.ProductDAO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class SearchDAOQueryDSL {
    private final EstablishmentDAOQueryDSL establishmentDAOQueryDSL;
    private final ProductDAOQueryDSL productDAOQueryDSL;


    public SearchDAOQueryDSL(EstablishmentDAOQueryDSL establishmentDAOQueryDSL, ProductDAOQueryDSL productDAOQueryDSL) {
        this.establishmentDAOQueryDSL = establishmentDAOQueryDSL;
        this.productDAOQueryDSL = productDAOQueryDSL;
    }

    public List<List<?>> searchEstablishmentAndProductByNameQueryDSL(String name) {
        return Arrays.asList(establishmentDAOQueryDSL.findBySimilarNameQueryDSL(name),
                productDAOQueryDSL.findBySimilarNameQueryDSL(name));
    }
}
