package com.fruitshop.ecommerce.service.impl;

import com.fruitshop.ecommerce.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        // For development: just log the email instead of sending it
        logger.info("Simulating password reset email:");
        logger.info("To: {}", to);
        logger.info("Reset Link: {}", resetLink);
    }
} 