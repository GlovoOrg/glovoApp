package com.api.glovoCRM.DTOs.EstablishmentDTOs;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentDTO {
    private Long id;
    private String name;
    private double priceOfDelivery;
    private int timeOfDelivery;
    private String image;
    private int rating;
    private int quantityOfRatings;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean isOpen;
    //todo вернется ли здесь list<ProductDTO>
    private List<ProductDTO> products;
}
