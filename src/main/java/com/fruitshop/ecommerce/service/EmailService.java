package com.fruitshop.ecommerce.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String resetLink);
} 