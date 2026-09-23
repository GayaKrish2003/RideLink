package com.ridelink.accountservice.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Runs once per incoming request. Checks for a valid JWT in the
// Authorization header, and if present and valid, tells Spring Security
// "this request is authenticated as this user, with this role" —
// so @PreAuthorize / role rules downstream can actually use it.
// If there's no token or it's invalid, this filter does nothing and
// lets the request continue — SecurityConfig's rules decide afterward
// whether an unauthenticated request is allowed through or rejected.
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Claims claims = jwtUtil.extractClaims(token);
                String userId = claims.get("userId", String.class);
                String role = claims.get("role", String.class);

                // Spring Security expects roles prefixed with "ROLE_" by convention.
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                var authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, authorities);

                // Registers this request as authenticated for the rest of
                // the request's lifecycle.
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Invalid/expired token — just don't authenticate.
                // SecurityConfig's rules will reject it if the endpoint requires auth.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}