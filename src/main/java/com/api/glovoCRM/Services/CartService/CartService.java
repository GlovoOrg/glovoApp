package com.api.glovoCRM.Services.CartService;

import com.api.glovoCRM.Repositories.Redis.CartRepository;
import com.api.glovoCRM.Repositories.Redis.CartItemRepository;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Models.OrderDetailModels.Cart;
import com.api.glovoCRM.Models.OrderDetailModels.CartItem;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.Services.BaseUserService;
import com.api.glovoCRM.Services.EstablishmentServices.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findCartByUserId(userId);
    }

    public Cart getOrCreateCartByUserId(Long userId) {
        return cartRepository.findCartByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = createNewCart(userId);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCartByUserId(userId);

        Optional<CartItem> isCartItemInCart = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (isCartItemInCart.isPresent()) {
            CartItem item = isCartItemInCart.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.recalculateTotal();
            cartItemRepository.save(item);
        } else {
            CartItem newItem = createCartItem(cart.getId(), productId, quantity);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cart.recalculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItemFromCart(Long userId, Long productId) {
        Cart cart = getOrCreateCartByUserId(userId);

        Optional<CartItem> itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (itemToUpdate.isPresent()) {
            CartItem item = itemToUpdate.get();
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                item.recalculateTotal();
                cartItemRepository.save(item);
            } else {
                cart.getItems().remove(item);
                cartItemRepository.deleteById(item.getId());
            }
            cart.recalculateTotal();
            return cartRepository.save(cart);
        } else {
            throw new SuchResourceNotFoundEx("Продукт с id:  " + productId + " не найден в корзине");
        }
    }

    @Transactional
    public void clearCartByUserId(Long userId) {
        Cart cart = getOrCreateCartByUserId(userId);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setTotalCharge(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private Cart createNewCart(Long userId) {
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID().toString());
        cart.setUserId(userId);
        return cart;
    }

    private CartItem createCartItem(String cartId, Long productId, int quantity) {
        CartItem item = new CartItem();
        item.setId(UUID.randomUUID().toString());
        item.setCartId(cartId);
        item.setProductId(productId);
        item.setQuantity(quantity);

        Product product = productService.findById(productId);
        if (product == null) {
            throw new SuchResourceNotFoundEx("Продукт с id:  " + productId + " не найден");
        }
        item.setOneProductPriceCart(product.getPrice());
        item.recalculateTotal();

        return item;
    }
}