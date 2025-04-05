package com.fruitshop.ecommerce.service;

import com.fruitshop.ecommerce.model.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    List<Product> getProductsByCategory(String category);
    List<Product> getOrganicProducts();
    List<Product> getProductsBySeasonality(String seasonality);
    boolean updateStock(Long productId, int quantity);
} 