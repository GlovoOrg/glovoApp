package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "subcategories")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubCategory extends BaseEntity {

    private String name;

    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "subcategory", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("name asc")
    private List<Establishment> establishment;
}
