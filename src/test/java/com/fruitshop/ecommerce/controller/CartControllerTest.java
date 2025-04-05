package com.fruitshop.ecommerce.controller;

import com.fruitshop.ecommerce.config.TestSecurityConfig;
import com.fruitshop.ecommerce.config.TestEmailConfig;
import com.fruitshop.ecommerce.model.Cart;
import com.fruitshop.ecommerce.model.CartItem;
import com.fruitshop.ecommerce.model.Product;
import com.fruitshop.ecommerce.service.CartService;
import com.fruitshop.ecommerce.util.TestJwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestSecurityConfig.class, TestEmailConfig.class})
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private TestJwtUtil testJwtUtil;

    private Cart cart;
    private CartItem cartItem;
    private Product product;
    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Apple");
        product.setPrice(new BigDecimal("1.99"));

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(new HashSet<>());
        cart.getCartItems().add(cartItem);
        cart.setTotalAmount(new BigDecimal("3.98"));

        userToken = testJwtUtil.generateTestToken("test@example.com", "USER");
        adminToken = testJwtUtil.generateTestToken("admin@example.com", "ADMIN");
    }

    @Test
    void getCart_ShouldReturnCart() throws Exception {
        when(cartService.getCartByUser(any())).thenReturn(cart);

        mockMvc.perform(get("/api/cart")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalAmount").value("3.98"));

        verify(cartService).getCartByUser(any());
    }

    @Test
    void addToCart_ShouldAddItemAndReturnCartItem() throws Exception {
        when(cartService.addItemToCart(any(), eq(1L), eq(2))).thenReturn(cartItem);

        mockMvc.perform(post("/api/cart/add")
                .header("Authorization", "Bearer " + userToken)
                .param("productId", "1")
                .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.product.name").value("Apple"));

        verify(cartService).addItemToCart(any(), eq(1L), eq(2));
    }

    @Test
    void updateCartItem_ShouldUpdateQuantityAndReturnCartItem() throws Exception {
        when(cartService.updateCartItem(any(), eq(1L), eq(3))).thenReturn(cartItem);

        mockMvc.perform(put("/api/cart/update/1")
                .header("Authorization", "Bearer " + userToken)
                .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));

        verify(cartService).updateCartItem(any(), eq(1L), eq(3));
    }

    @Test
    void removeCartItem_ShouldRemoveItem() throws Exception {
        doNothing().when(cartService).removeCartItem(any(), eq(1L));

        mockMvc.perform(delete("/api/cart/remove/1")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        verify(cartService).removeCartItem(any(), eq(1L));
    }

    @Test
    void clearCart_ShouldClearAllItems() throws Exception {
        doNothing().when(cartService).clearCart(any());

        mockMvc.perform(delete("/api/cart/clear")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        verify(cartService).clearCart(any());
    }

    @Test
    void unauthorizedAccess_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(cartService);
    }

    @Test
    void addToCart_WithInvalidQuantity_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/cart/add")
                .header("Authorization", "Bearer " + userToken)
                .param("productId", "1")
                .param("quantity", "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }
} 