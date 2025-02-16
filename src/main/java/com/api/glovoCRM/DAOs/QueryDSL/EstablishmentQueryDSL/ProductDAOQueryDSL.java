package com.api.glovoCRM.DAOs.QueryDSL.EstablishmentQueryDSL;

import com.api.glovoCRM.DAOs.QueryDSL.BaseDAOQueryDSL;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Models.EstablishmentModels.QProduct;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDAOQueryDSL extends BaseDAOQueryDSL<Product, QProduct> {
    @Override
    protected QProduct getQEntity() {
        return QProduct.product;
    }
}
