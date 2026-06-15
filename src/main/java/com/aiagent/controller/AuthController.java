package com.aiagent.controller;

import com.aiagent.dto.AuthRequest;
import com.aiagent.dto.AuthResponse;
import com.aiagent.entity.User;
import com.aiagent.service.JwtService;
import com.aiagent.service.UserService;
import com.apiresponse.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody AuthRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<AuthResponse>builder()
                    .success(false)
                    .message("Username already exists")
                    .statusCode(400)
                    .build()
            );
        }

        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<AuthResponse>builder()
                    .success(false)
                    .message("Email already exists")
                    .statusCode(400)
                    .build()
            );
        }

        User user = userService.createUser(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            request.getFirstName(),
            request.getLastName()
        );

        String token = jwtService.generateToken(user.getUsername(), user.getId());

        AuthResponse response = AuthResponse.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .token(token)
            .success(true)
            .message("User registered successfully")
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Registration successful")
                .data(response)
                .statusCode(201)
                .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        Optional<User> user = userService.findByUsername(request.getUsername());

        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<AuthResponse>builder()
                    .success(false)
                    .message("Invalid username or password")
                    .statusCode(401)
                    .build()
            );
        }

        String token = jwtService.generateToken(user.get().getUsername(), user.get().getId());

        AuthResponse response = AuthResponse.builder()
            .userId(user.get().getId())
            .username(user.get().getUsername())
            .email(user.get().getEmail())
            .token(token)
            .success(true)
            .message("Login successful")
            .build();

        return ResponseEntity.ok(
            ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .statusCode(200)
                .build()
        );
    }
}
