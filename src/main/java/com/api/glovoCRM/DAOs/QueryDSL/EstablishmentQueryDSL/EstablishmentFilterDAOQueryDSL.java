package com.api.glovoCRM.DAOs.QueryDSL.EstablishmentQueryDSL;

import com.api.glovoCRM.DAOs.QueryDSL.BaseDAOQueryDSL;
import com.api.glovoCRM.Models.EstablishmentModels.EstablishmentFilter;
import com.api.glovoCRM.Models.EstablishmentModels.QEstablishmentFilter;
import org.springframework.stereotype.Repository;

@Repository
public class EstablishmentFilterDAOQueryDSL extends BaseDAOQueryDSL<EstablishmentFilter, QEstablishmentFilter> {
    @Override
    protected QEstablishmentFilter getQEntity() {
        return QEstablishmentFilter.establishmentFilter;
    }
}
