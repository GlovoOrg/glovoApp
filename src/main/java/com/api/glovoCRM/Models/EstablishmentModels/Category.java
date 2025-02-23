package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {

    @NotNull(message = "Название категории не может быть null")
    @NotBlank(message = "Название категории не может быть пустым")
    @Size(max = 355, message = "Максимальная длина названия — 355 символов")
    @Column(name = "name",nullable = false, length = 355)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("name asc")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SubCategory> subCategories = new ArrayList<>();
}
