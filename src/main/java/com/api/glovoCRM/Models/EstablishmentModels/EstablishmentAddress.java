package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "establishment_adresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentAddress extends BaseEntity {

    private String addressLine;

    private String featureToGetThere;

    private double latitude;

    private double longitude;

    @OneToOne
    @JoinColumn(name = "establishment_id")
    private Establishment establishment;
}
