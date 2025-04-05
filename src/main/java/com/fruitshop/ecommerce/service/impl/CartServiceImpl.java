package com.fruitshop.ecommerce.service.impl;

import com.fruitshop.ecommerce.model.Cart;
import com.fruitshop.ecommerce.model.CartItem;
import com.fruitshop.ecommerce.model.Product;
import com.fruitshop.ecommerce.model.User;
import com.fruitshop.ecommerce.repository.CartRepository;
import com.fruitshop.ecommerce.repository.UserRepository;
import com.fruitshop.ecommerce.service.CartService;
import com.fruitshop.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Override
    public Cart getCartByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        return cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));
    }

    @Override
    @Transactional
    public CartItem addItemToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUser(userId);
        Product product = productService.getProductById(productId);

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getCartItems().add(cartItem);
        }

        cart.updateTotalAmount();
        cartRepository.save(cart);
        return cartItem;
    }

    @Override
    @Transactional
    public CartItem updateCartItem(Long userId, Long cartItemId, Integer quantity) {
        Cart cart = getCartByUser(userId);
        
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        cartItem.setQuantity(quantity);
        cart.updateTotalAmount();
        cartRepository.save(cart);
        return cartItem;
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        Cart cart = getCartByUser(userId);
        
        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        cart.updateTotalAmount();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUser(userId);
        cart.getCartItems().clear();
        cart.updateTotalAmount();
        cartRepository.save(cart);
    }

    private Cart createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
} 