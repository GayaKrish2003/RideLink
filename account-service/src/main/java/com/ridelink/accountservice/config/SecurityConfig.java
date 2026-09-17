package com.ridelink.accountservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Defines how Spring Security handles incoming HTTP requests.
    // TEMPORARY: this permits ALL requests without authentication so we
    // can test JWT generation before real login/register endpoints exist.
    // Must be replaced with proper role-based rules (PASSENGER / DRIVER / ADMIN)
    // once /register and /login are built.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection — not needed for a stateless REST API
                // that uses JWTs instead of session cookies.
                .csrf(csrf -> csrf.disable())

                // Allow every request through without authentication (temporary).
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}