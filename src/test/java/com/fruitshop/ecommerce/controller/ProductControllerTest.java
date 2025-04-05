package com.fruitshop.ecommerce.controller;

import com.fruitshop.ecommerce.model.Product;
import com.fruitshop.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product product;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Apple");
        product.setDescription("Fresh red apples");
        product.setPrice(new BigDecimal("1.99"));
        product.setStockQuantity(100);
        product.setCategory("Fruits");
        product.setIsOrganic(true);
        product.setSeasonality("All Year");

        products = Arrays.asList(product);
    }

    @Test
    void getAllProducts_ShouldReturnProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[0].price").value("1.99"));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Apple"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_WithAdminRole_ShouldCreateProduct() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Apple\",\"description\":\"Fresh red apples\",\"price\":1.99," +
                        "\"stockQuantity\":100,\"category\":\"Fruits\",\"isOrganic\":true,\"seasonality\":\"All Year\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Apple"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProduct_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Apple\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProductsByCategory_ShouldReturnProducts() throws Exception {
        when(productService.getProductsByCategory("Fruits")).thenReturn(products);

        mockMvc.perform(get("/api/products/category/Fruits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Fruits"));
    }

    @Test
    void getOrganicProducts_ShouldReturnOrganicProducts() throws Exception {
        when(productService.getOrganicProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products/organic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isOrganic").value(true));
    }
} 