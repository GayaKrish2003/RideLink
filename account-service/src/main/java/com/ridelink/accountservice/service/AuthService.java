package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.RegisterRequest;
import com.ridelink.accountservice.dto.RegisterResponse;
import com.ridelink.accountservice.model.User;
import com.ridelink.accountservice.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthService {

    private final UserRepository userRepository;

    // BCryptPasswordEncoder handles password hashing.
    // Each call to .encode() automatically generates a unique salt,
    // so two users with the same password get different hashes.
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Spring injects UserRepository automatically via constructor.
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterResponse register(RegisterRequest request) {

        // Reject registration if the email is already taken.
        // 409 Conflict is the correct HTTP status for "this already exists".
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        // Build the User entity. Note: we hash the password here —
        // the plain text version from the request is never stored or logged.
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(User.Status.ACTIVE) // new accounts start as ACTIVE
                .build();

        User savedUser = userRepository.save(user);

        // Convert the saved entity into the safe response shape
        // (excludes passwordHash) before returning it to the controller.
        return RegisterResponse.fromUser(savedUser);
    }
}