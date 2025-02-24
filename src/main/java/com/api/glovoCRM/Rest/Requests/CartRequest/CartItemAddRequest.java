package com.api.glovoCRM.Rest.Requests.CartRequest;

import lombok.Data;

@Data
public class CartItemAddRequest {
    private Long userId;
    private Long productId;
    private int quantity;
}
