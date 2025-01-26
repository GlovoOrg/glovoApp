package com.api.glovoCRM.Rest.Requests;

import lombok.Data;

@Data
public class DiscountProductRequest {
    private int discount;
    private Boolean active;
}
