package com.aiagent.service;

import com.aiagent.dto.request.LoginRequest;
import com.aiagent.dto.request.RegisterRequest;
import com.aiagent.dto.response.AuthResponse;
import com.aiagent.model.User;
import com.aiagent.model.enums.Role;
import com.aiagent.repository.UserRepository;
import com.aiagent.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return AuthResponse.builder()
                    .message("Passwords do not match")
                    .build();
        }

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return AuthResponse.builder()
                    .message("Email already registered")
                    .build();
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().toString())
                .message("Registration successful")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return AuthResponse.builder()
                    .message("Invalid email or password")
                    .build();
        }

        User loggedInUser = user.get();
        String token = jwtUtil.generateToken(loggedInUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(loggedInUser.getId())
                .name(loggedInUser.getName())
                .email(loggedInUser.getEmail())
                .role(loggedInUser.getRole().toString())
                .message("Login successful")
                .build();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
