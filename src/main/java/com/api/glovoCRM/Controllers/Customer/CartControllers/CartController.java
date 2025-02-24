package com.api.glovoCRM.Controllers.Customer.CartControllers;
import com.api.glovoCRM.Models.OrderDetailModels.Cart;
import com.api.glovoCRM.Rest.Requests.CartRequest.CartItemAddRequest;
import com.api.glovoCRM.Rest.Requests.CartRequest.CartItemDeleteRequest;
import com.api.glovoCRM.Services.CartService.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getOrCreateCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/addItem")
    public ResponseEntity<Cart> addToCart(@RequestBody CartItemAddRequest request) {
        Cart updatedCart = cartService.addItemToCart(request.getUserId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/removeItem")
    public ResponseEntity<Cart> removeItemFromCart(@RequestBody CartItemDeleteRequest request) {
        Cart updatedCart = cartService.removeItemFromCart(request.getUserId(), request.getProductId());
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCartByUserId(userId);
        return ResponseEntity.ok().build();
    }

}
