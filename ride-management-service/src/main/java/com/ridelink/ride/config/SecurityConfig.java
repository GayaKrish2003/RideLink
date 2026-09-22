package com.ridelink.ride.config;

import com.ridelink.ride.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public: health check + API docs so Swagger UI works without a token
                .requestMatchers("/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Creating a ride request: passenger only
                .requestMatchers(HttpMethod.POST, "/api/rides").hasRole("PASSENGER")
                // Status transitions: driver or admin (a passenger may only cancel - handled in service logic)
                .requestMatchers(HttpMethod.PATCH, "/api/rides/*/status").hasAnyRole("PASSENGER", "DRIVER", "ADMIN")
                // Reading rides: any authenticated role
                .requestMatchers(HttpMethod.GET, "/api/rides/**").hasAnyRole("PASSENGER", "DRIVER", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
