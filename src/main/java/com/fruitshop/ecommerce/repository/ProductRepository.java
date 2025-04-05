package com.fruitshop.ecommerce.repository;

import com.fruitshop.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByIsOrganic(Boolean isOrganic);
    List<Product> findBySeasonality(String seasonality);
} 