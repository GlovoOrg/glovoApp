package com.api.glovoCRM.Rest.Requests.OrderRequests;

import lombok.Data;

@Data
public class OrderAddressRequest {
    private String addressLine;
    private double latitude;
    private double longitude;
}
