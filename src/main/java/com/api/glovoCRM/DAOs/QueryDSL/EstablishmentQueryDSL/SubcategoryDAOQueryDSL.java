package com.api.glovoCRM.DAOs.QueryDSL.EstablishmentQueryDSL;

import com.api.glovoCRM.DAOs.QueryDSL.BaseDAOQueryDSL;
import com.api.glovoCRM.Models.EstablishmentModels.QSubCategory;
import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import org.springframework.stereotype.Repository;

@Repository
public class SubcategoryDAOQueryDSL extends BaseDAOQueryDSL<SubCategory, QSubCategory> {
    @Override
    protected QSubCategory getQEntity() {
        return QSubCategory.subCategory;
    }
}
