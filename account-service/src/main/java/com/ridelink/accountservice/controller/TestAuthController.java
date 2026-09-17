package com.ridelink.accountservice.controller;

import com.ridelink.accountservice.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// TEMPORARY controller — only exists to manually verify JWT generation works.
// Delete this once the real /register and /login endpoints are built,
// since those will be the actual places tokens get issued.
@RestController
@RequestMapping("/api/auth")
public class TestAuthController {

    private final JwtUtil jwtUtil;

    // Spring injects the JwtUtil bean automatically via constructor.
    public TestAuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Generates a token for a random fake user, just to confirm
    // the claims (userId, role, iat, exp) come out correctly.
    @GetMapping("/test-token")
    public String testToken() {
        return jwtUtil.generateToken(UUID.randomUUID(), "PASSENGER");
    }
}