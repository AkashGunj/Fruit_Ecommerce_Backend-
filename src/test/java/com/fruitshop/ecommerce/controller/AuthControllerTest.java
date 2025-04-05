package com.fruitshop.ecommerce.controller;

import com.fruitshop.ecommerce.model.Role;
import com.fruitshop.ecommerce.model.User;
import com.fruitshop.ecommerce.payload.request.LoginRequest;
import com.fruitshop.ecommerce.payload.request.SignupRequest;
import com.fruitshop.ecommerce.repository.RoleRepository;
import com.fruitshop.ecommerce.repository.UserRepository;
import com.fruitshop.ecommerce.security.jwt.JwtUtils;
import com.fruitshop.ecommerce.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private EmailService emailService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private Role userRole;
    private User user;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setEmail("john@example.com");
        signupRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(Role.ERole.ROLE_USER);

        user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
    }

    @Test
    void whenSignupWithValidData_thenReturns200() throws Exception {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(roleRepository.findByName(Role.ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void whenSignupWithExistingEmail_thenReturns400() throws Exception {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }

    @Test
    void whenForgotPasswordWithValidEmail_thenReturns200() throws Exception {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/forgot-password")
                .param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset link sent to email"));
    }
} 