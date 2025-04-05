package com.fruitshop.ecommerce.service.impl;

import com.fruitshop.ecommerce.model.Product;
import com.fruitshop.ecommerce.repository.ProductRepository;
import com.fruitshop.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setIsOrganic(product.getIsOrganic());
        existingProduct.setSeasonality(product.getSeasonality());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> getOrganicProducts() {
        return productRepository.findByIsOrganic(true);
    }

    @Override
    public List<Product> getProductsBySeasonality(String seasonality) {
        return productRepository.findBySeasonality(seasonality);
    }

    @Override
    @Transactional
    public boolean updateStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        int newStock = product.getStockQuantity() - quantity;
        
        if (newStock < 0) {
            return false;
        }
        
        product.setStockQuantity(newStock);
        productRepository.save(product);
        return true;
    }
} 