package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Column(name = "image_url")
    private String image;

    private int rating; // 1 to 5

    private int quantityOfRatings; // max = 500

    private int timeOfDelivery;
    
    private LocalTime openTime;

    private LocalTime closeTime;

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("name asc")
    private List<Product> product;

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("name asc")
    private List<EstablishmentFilter> establishment_filter;

    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private SubCategory subcategory;

    @OneToOne(mappedBy = "establishment", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private EstablishmentAddress establishmentAddress;

    public boolean getIsOpen() {
        LocalTime now = LocalTime.now();
        return now.isAfter(this.openTime) && now.isBefore(this.closeTime);
    }
}
