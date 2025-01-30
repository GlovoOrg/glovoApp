package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "subcategories")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubCategory extends BaseEntity {
    @NotNull(message = "Название подкатегории не может быть null")
    @NotBlank(message = "Название подкатегории обязательно")
    @Size(max = 355, message = "Максимальная длина названия — 355 символов")
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @NotNull(message = "Категория обязательна")
    private Category category;

    @OneToMany(mappedBy = "subcategory", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("name asc")
    private List<Establishment> establishments = new ArrayList<>();
}
