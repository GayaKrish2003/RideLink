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
    // once /login is built.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection — not needed for a stateless REST API
                // that uses JWTs instead of session cookies.
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Always allow Swagger's own pages through, even after
                        // we lock down real endpoints later with role-based rules.
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // TEMPORARY: still permit-all everywhere else for now,
                        // until /login exists and we add real role-based rules.
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}