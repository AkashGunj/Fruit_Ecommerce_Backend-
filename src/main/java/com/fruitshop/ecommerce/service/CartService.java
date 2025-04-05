package com.fruitshop.ecommerce.service;

import com.fruitshop.ecommerce.model.Cart;
import com.fruitshop.ecommerce.model.CartItem;

public interface CartService {
    Cart getCartByUser(Long userId);
    CartItem addItemToCart(Long userId, Long productId, Integer quantity);
    CartItem updateCartItem(Long userId, Long cartItemId, Integer quantity);
    void removeCartItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
} 