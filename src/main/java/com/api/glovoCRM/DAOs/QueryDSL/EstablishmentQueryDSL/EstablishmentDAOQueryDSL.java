package com.api.glovoCRM.DAOs.QueryDSL.EstablishmentQueryDSL;

import com.api.glovoCRM.DAOs.QueryDSL.BaseDAOQueryDSL;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.QEstablishment;
import org.springframework.stereotype.Repository;

@Repository
public class EstablishmentDAOQueryDSL  extends BaseDAOQueryDSL<Establishment, QEstablishment> {
    @Override
    protected QEstablishment getQEntity() {
        return QEstablishment.establishment;
    }
}
