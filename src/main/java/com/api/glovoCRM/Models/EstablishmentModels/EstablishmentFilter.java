package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "establishment_filters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentFilter extends BaseEntity {

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "establishment_id")
    private Establishment establishment;

    @ManyToMany(mappedBy = "establishmentFilters")
    @OrderBy("name asc")
    private List<Product> products;
}
