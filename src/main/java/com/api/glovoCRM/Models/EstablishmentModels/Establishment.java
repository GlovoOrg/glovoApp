package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "establishments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Establishment extends BaseEntity {

    private String name;

    private double priceOfDelivery;

    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;

    private int rating;

    private int quantityOfRatings;

    private int timeOfDelivery;
    
    private LocalDateTime openTime;

    private LocalDateTime closeTime;

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name asc")
    private List<Product> product;

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name asc")
    private List<EstablishmentFilter> establishment_filter;

    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private SubCategory subcategory;

    @OneToOne(mappedBy = "establishment", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private EstablishmentAddress establishmentAddress;

    public boolean getIsOpen() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(this.openTime) && now.isBefore(this.closeTime);
    }
}
