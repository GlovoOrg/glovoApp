package com.api.glovoCRM.Rest.Requests.CartRequest;

import lombok.Data;

@Data
public class CartItemDeleteRequest {
    private Long userId;
    private Long productId;
}
