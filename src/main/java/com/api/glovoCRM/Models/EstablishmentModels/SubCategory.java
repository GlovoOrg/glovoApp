package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Slf4j
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

    @Transient
    private Long categoryId;

    @PreRemove
    private void preRemove() {
        log.debug("Вызов @PreRemove для подкатегории ID: {}", this.getId());
        if (category != null) {
            category.getSubCategories().remove(this);
            log.debug("Подкатегория удалена из категории ID: {}", category.getId());
        }
    }
    @PreUpdate
    private void preUpdate() {
        log.debug("Вызов @PreUpdate для подкатегории ID: {}", this.getId());
        if (category != null) {
            category.getSubCategories().remove(this);
            category.getSubCategories().add(this);
            log.debug("Подкатегория обновлена в категории ID: {}", category.getId());
        }
    }
    @PrePersist
    private void linkCategory(){
        if(this.category == null && this.categoryId != null){
            this.category = new Category();
            this.category.setId(this.categoryId);
        }
    }
}
