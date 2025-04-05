package com.fruitshop.ecommerce.controller;

import com.fruitshop.ecommerce.model.Cart;
import com.fruitshop.ecommerce.model.CartItem;
import com.fruitshop.ecommerce.security.services.UserDetailsImpl;
import com.fruitshop.ecommerce.service.CartService;
import com.fruitshop.ecommerce.validation.PositiveQuantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Cart cart = cartService.getCartByUser(userDetails.getId());
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestParam Long productId,
            @Valid @PositiveQuantity @RequestParam Integer quantity) {
        CartItem cartItem = cartService.addItemToCart(userDetails.getId(), productId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long cartItemId,
            @Valid @PositiveQuantity @RequestParam Integer quantity) {
        CartItem cartItem = cartService.updateCartItem(userDetails.getId(), cartItemId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<?> removeCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long cartItemId) {
        cartService.removeCartItem(userDetails.getId(), cartItemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        cartService.clearCart(userDetails.getId());
        return ResponseEntity.ok().build();
    }
} 