package com.api.glovoCRM.Rest.Requests.ProductRequests;

import com.api.glovoCRM.Rest.Requests.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductWithDiscountPatchRequest extends BaseRequest {
    private String description;
    private BigDecimal price;
    private Long establishmentId;
    private Long establishmentFilterId;
    private int discount;
    private Boolean active;
}
