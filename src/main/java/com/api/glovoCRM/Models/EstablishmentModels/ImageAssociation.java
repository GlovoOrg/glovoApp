package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.constants.EntityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "image_associations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageAssociation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    private Long ownerId;

}
