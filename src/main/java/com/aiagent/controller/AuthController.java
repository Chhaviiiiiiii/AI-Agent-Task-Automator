package com.aiagent.controller;

import com.aiagent.dto.request.LoginRequest;
import com.aiagent.dto.request.RegisterRequest;
import com.aiagent.dto.response.ApiResponse;
import com.aiagent.dto.response.AuthResponse;
import com.aiagent.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500", "http://localhost:5500"})
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse authResponse = userService.register(request);
            if (authResponse.getToken() != null) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse(201, authResponse.getMessage(), authResponse));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(400, authResponse.getMessage(), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "Registration failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse authResponse = userService.login(request);
            if (authResponse.getToken() != null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(200, authResponse.getMessage(), authResponse));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(401, authResponse.getMessage(), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "Login failed: " + e.getMessage(), null));
        }
    }
}
