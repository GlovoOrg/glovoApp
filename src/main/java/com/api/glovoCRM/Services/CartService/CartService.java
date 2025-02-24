package com.api.glovoCRM.Services.CartService;

import com.api.glovoCRM.DAOs.Redis.CartDAO;
import com.api.glovoCRM.DAOs.Redis.CartItemDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Models.OrderDetailModels.Cart;
import com.api.glovoCRM.Models.OrderDetailModels.CartItem;
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
    private final CartDAO cartDAO;
    private final CartItemDAO cartItemDAO;
    private final ProductService productService;

    @Autowired
    public CartService(CartDAO cartDAO, CartItemDAO cartItemDAO, ProductService productService) {
        this.cartDAO = cartDAO;
        this.cartItemDAO = cartItemDAO;
        this.productService = productService;
    }

    // Оставляем метод для получения корзины без создания
    public Optional<Cart> getCartByUserId(Long userId) {
        return cartDAO.findCartByUserId(userId);
    }

    // Добавляем метод для получения или создания корзины
    public Cart getOrCreateCartByUserId(Long userId) {
        return cartDAO.findCartByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = createNewCart(userId);
                    return cartDAO.save(newCart); // Сохраняем сразу в Redis
                });
    }

    @Transactional
    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        // Используем getOrCreateCartByUserId вместо проверки Optional
        Cart cart = getOrCreateCartByUserId(userId);

        Optional<CartItem> isCartItemInCart = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (isCartItemInCart.isPresent()) {
            CartItem item = isCartItemInCart.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.recalculateTotal();
            cartItemDAO.save(item);
        } else {
            CartItem newItem = createCartItem(cart.getId(), productId, quantity);
            cart.getItems().add(newItem);
            cartItemDAO.save(newItem);
        }

        cart.recalculateTotal();
        return cartDAO.save(cart);
    }

    private Cart createNewCart(Long userId) {
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID().toString());
        cart.setUserId(userId);
        return cart; // Теперь сохранение происходит в getOrCreateCartByUserId
    }

    private CartItem createCartItem(String cartId, Long productId, int quantity) {
        CartItem item = new CartItem();
        item.setId(UUID.randomUUID().toString());
        item.setCartId(cartId);
        item.setProductId(productId);
        item.setQuantity(quantity);

        Product product = productService.findById(productId);
        if (product == null) {
            throw new SuchResourceNotFoundEx("Product with id " + productId + " not found");
        }
        item.setOneProductPriceCart(product.getPrice());
        item.recalculateTotal();

        return item;
    }

    @Transactional
    public void removeCartItemFromCart(String cartId, String cartItemId) {
        Cart cart = cartDAO.findById(cartId)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Cart not found"));
        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cart.recalculateTotal();
        cartDAO.save(cart);
        cartItemDAO.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(String cartId) {
        Cart cart = cartDAO.findById(cartId)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Cart not found"));

        cartItemDAO.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setTotalCharge(BigDecimal.ZERO);
        cartDAO.save(cart);
    }
}