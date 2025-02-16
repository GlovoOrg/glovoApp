package com.api.glovoCRM.DAOs.QueryDSL.EstablishmentQueryDSL;

import com.api.glovoCRM.DAOs.QueryDSL.BaseDAOQueryDSL;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Models.EstablishmentModels.QCategory;
import org.springframework.stereotype.Repository;

@Repository
public class CategoriesDAOQueryDSL extends BaseDAOQueryDSL<Category, QCategory> {
    @Override
    protected QCategory getQEntity() {
        return QCategory.category;
    }

}
