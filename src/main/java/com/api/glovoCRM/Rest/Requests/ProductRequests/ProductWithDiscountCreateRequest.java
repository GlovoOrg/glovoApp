package com.api.glovoCRM.Rest.Requests.ProductRequests;

import com.api.glovoCRM.Rest.Requests.BaseRequestNotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductWithDiscountCreateRequest extends BaseRequestNotNull {
    private String description;
    private BigDecimal price;
    private Long establishmentId;
    private Long establishmentFilterId;
    private int discount;
    private Boolean active;
}
