package com.fruitshop.ecommerce.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.fruitshop.ecommerce.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TestJwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    public String generateTestToken(String username, String... roles) {
        // Convert roles to proper format if they don't already have the prefix
        String[] formattedRoles = Arrays.stream(roles)
            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
            .toArray(String[]::new);

        UserDetailsImpl userDetails = new UserDetailsImpl(
            1L,
            username,
            "password",
            "Test",
            "User",
            Arrays.stream(formattedRoles)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList())
        );

        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .claim("roles", userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList()))
            .claim("id", userDetails.getId())
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
} 