package com.api.glovoCRM.Controllers.Customer.CartControllers;
import com.api.glovoCRM.Models.OrderDetailModels.Cart;
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
    public ResponseEntity<Cart> addToCart(@RequestParam Long userId,
                                          @RequestParam Long productId,
                                          @RequestParam int quantity) {
        Cart updatedCart = cartService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }


    @DeleteMapping("/{cartId}/item/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable String cartId,
                                           @PathVariable String itemId) {
        cartService.removeCartItemFromCart(cartId, itemId);
        return ResponseEntity.ok().build();
    }

}
