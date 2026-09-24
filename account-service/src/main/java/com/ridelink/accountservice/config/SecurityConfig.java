package com.ridelink.accountservice.config;

import com.ridelink.accountservice.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Expose BCryptPasswordEncoder as a proper Spring Bean, so it can be
    // injected into AuthService instead of manually instantiated there.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // Explicitly disable Spring's default login form and basic auth —
                // we authenticate purely via JWT, not these mechanisms.
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                // Stateless — we use JWTs, not server-side sessions.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Public endpoints — no token needed
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Profile and deactivation are for any authenticated user —
                        // PASSENGER, DRIVER, or ADMIN can all view/update their own
                        // profile and deactivate their own account.
                        .requestMatchers("/api/auth/profile", "/api/auth/deactivate")
                        .hasAnyRole("PASSENGER", "DRIVER", "ADMIN")

                        // Everything else requires authentication at minimum
                        // (a safe default for any future endpoint added later).
                        .anyRequest().authenticated()
                )

                // Run our JWT filter before Spring's default login filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}