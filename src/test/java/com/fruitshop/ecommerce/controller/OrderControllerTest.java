package com.fruitshop.ecommerce.controller;

import com.fruitshop.ecommerce.config.TestSecurityConfig;
import com.fruitshop.ecommerce.config.TestEmailConfig;
import com.fruitshop.ecommerce.model.Order;
import com.fruitshop.ecommerce.model.OrderItem;
import com.fruitshop.ecommerce.model.Product;
import com.fruitshop.ecommerce.model.User;
import com.fruitshop.ecommerce.service.OrderService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestSecurityConfig.class, TestEmailConfig.class})
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private TestJwtUtil testJwtUtil;

    private Order order;
    private List<Order> orders;
    private User user;
    private Product product;
    private OrderItem orderItem;
    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Apple");
        product.setPrice(new BigDecimal("1.99"));

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(product.getPrice());

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setShippingAddress("123 Test St");
        order.setTotalAmount(new BigDecimal("3.98"));
        order.setOrderItems(new HashSet<>(Arrays.asList(orderItem)));

        orders = Arrays.asList(order);

        userToken = testJwtUtil.generateTestToken("test@example.com", "USER");
        adminToken = testJwtUtil.generateTestToken("admin@example.com", "ADMIN");
    }

    @Test
    void createOrder_ShouldCreateAndReturnOrder() throws Exception {
        when(orderService.createOrder(any(), eq("123 Test St"))).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + userToken)
                .param("shippingAddress", "123 Test St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.shippingAddress").value("123 Test St"));

        verify(orderService).createOrder(any(), eq("123 Test St"));
    }

    @Test
    void getOrder_ShouldReturnOrder() throws Exception {
        when(orderService.getOrderById(eq(1L), any())).thenReturn(order);

        mockMvc.perform(get("/api/orders/1")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService).getOrderById(eq(1L), any());
    }

    @Test
    void getUserOrders_ShouldReturnOrders() throws Exception {
        when(orderService.getUserOrders(any())).thenReturn(orders);

        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderService).getUserOrders(any());
    }

    @Test
    void updateOrderStatus_WithAdminRole_ShouldUpdateStatus() throws Exception {
        order.setStatus(Order.OrderStatus.PROCESSING);
        when(orderService.updateOrderStatus(eq(1L), eq(Order.OrderStatus.PROCESSING))).thenReturn(order);

        mockMvc.perform(put("/api/orders/1/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "PROCESSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        verify(orderService).updateOrderStatus(eq(1L), eq(Order.OrderStatus.PROCESSING));
    }

    @Test
    void updateOrderStatus_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/orders/1/status")
                .header("Authorization", "Bearer " + userToken)
                .param("status", "PROCESSING"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(orderService);
    }

    @Test
    void unauthorizedAccess_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(orderService);
    }
} 