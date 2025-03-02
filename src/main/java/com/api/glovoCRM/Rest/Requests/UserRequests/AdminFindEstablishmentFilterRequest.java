package com.api.glovoCRM.Rest.Requests.UserRequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminFindEstablishmentFilterRequest {
    private Long id;
    private String name;
    private double priceOfDelivery;
    private double totalRating;
    private LocalTime openTime;
    private LocalTime closeTime;
}
