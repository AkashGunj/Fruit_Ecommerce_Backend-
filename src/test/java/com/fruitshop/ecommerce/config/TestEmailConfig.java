package com.fruitshop.ecommerce.config;

import com.fruitshop.ecommerce.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestEmailConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestEmailConfig.class);

    @Bean
    @Primary
    public EmailService emailService() {
        return new EmailService() {
            @Override
            public void sendPasswordResetEmail(String to, String resetLink) {
                logger.info("TEST - Sending password reset email to: {} with link: {}", to, resetLink);
            }
        };
    }
} 