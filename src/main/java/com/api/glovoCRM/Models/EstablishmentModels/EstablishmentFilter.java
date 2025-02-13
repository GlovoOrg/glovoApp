package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Название фильтра обязательно")
    @Size(max = 355, message = "Максимальная длина названия — 355 символов")
    @Column(name = "name", length = 355, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "establishment_id")
    @NotNull(message = "Заведение обязательно")
    private Establishment establishment;

    @ManyToMany(mappedBy = "establishmentFilters")
    @OrderBy("name asc")
    private List<Product> products;
}
