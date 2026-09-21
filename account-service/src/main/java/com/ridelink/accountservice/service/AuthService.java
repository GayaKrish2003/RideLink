package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.RegisterRequest;
import com.ridelink.accountservice.dto.RegisterResponse;
import com.ridelink.accountservice.model.User;
import com.ridelink.accountservice.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.ridelink.accountservice.dto.LoginRequest;
import com.ridelink.accountservice.dto.LoginResponse;
import com.ridelink.accountservice.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // BCryptPasswordEncoder handles password hashing.
    // Each call to .encode() automatically generates a unique salt,
    // so two users with the same password get different hashes.
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Spring injects UserRepository automatically via constructor.(also accept JwtUtil)
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponse register(RegisterRequest request) {

        // Prevent public self-registration as ADMIN — admin accounts should
        // only be created through a separate, protected process (not implemented
        // in this assignment's scope, but blocked here for security correctness).
        if (request.getRole() == User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot self-register as ADMIN");
        }

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

    public LoginResponse login(LoginRequest request) {

        // Look up the user by email. If not found, we deliberately use the SAME
        // error message as a wrong password below — this prevents an attacker
        // from figuring out which emails are registered just by probing the
        // login endpoint (a basic security practice called avoiding "user enumeration").
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        // Compare the submitted plain-text password against the stored hash.
        // BCryptPasswordEncoder.matches() hashes the input the same way and
        // compares — you never decrypt the stored hash, hashing is one-way.
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // reject login if the account isn't active
        // someone suspended/deactivated shouldn't be able to log in
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }

        // Credentials check passed — generate a real JWT for this user.
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name());

        return LoginResponse.builder()
                .token(token)
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}