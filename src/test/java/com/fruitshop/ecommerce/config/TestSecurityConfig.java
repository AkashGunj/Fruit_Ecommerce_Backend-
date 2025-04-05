package com.fruitshop.ecommerce.config;

import com.fruitshop.ecommerce.model.Role;
import com.fruitshop.ecommerce.model.User;
import com.fruitshop.ecommerce.repository.RoleRepository;
import com.fruitshop.ecommerce.repository.UserRepository;
import com.fruitshop.ecommerce.security.jwt.AuthEntryPointJwt;
import com.fruitshop.ecommerce.security.jwt.AuthTokenFilter;
import com.fruitshop.ecommerce.security.jwt.JwtUtils;
import com.fruitshop.ecommerce.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashSet;
import java.util.Set;

@TestConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(101)
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @Autowired
    private UserRepository userRepository;

    @Bean
    @Primary
    public JwtUtils jwtUtils() {
        JwtUtils jwtUtils = new JwtUtils();
        jwtUtils.setJwtSecret(jwtSecret);
        jwtUtils.setJwtExpirationMs(jwtExpirationMs);
        return jwtUtils;
    }

    @Bean
    @Primary
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    @Primary
    public AuthEntryPointJwt unauthorizedHandler() {
        return new AuthEntryPointJwt();
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return email -> {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found with email: " + email));
            return UserDetailsImpl.build(user);
        };
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler()).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers("/api/products/**").permitAll()
            .antMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @TestConfiguration
    public static class TestDataInitializer {
        @Autowired
        private RoleRepository roleRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Bean
        public void initializeTestData() {
            // Create roles if they don't exist
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(Role.ERole.ROLE_USER);
                    return roleRepository.save(role);
                });

            Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(Role.ERole.ROLE_ADMIN);
                    return roleRepository.save(role);
                });

            // Create test user if it doesn't exist
            if (!userRepository.existsByEmail("test@example.com")) {
                User user = new User();
                user.setEmail("test@example.com");
                user.setPassword(passwordEncoder.encode("password"));
                user.setFirstName("Test");
                user.setLastName("User");
                Set<Role> roles = new HashSet<>();
                roles.add(userRole);
                user.setRoles(roles);
                userRepository.save(user);
            }

            // Create test admin if it doesn't exist
            if (!userRepository.existsByEmail("admin@example.com")) {
                User admin = new User();
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("password"));
                admin.setFirstName("Test");
                admin.setLastName("Admin");
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRoles(roles);
                userRepository.save(admin);
            }
        }
    }
} 