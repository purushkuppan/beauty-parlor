package com.beautyparlour.service;

import com.beautyparlour.exception.AppException;
import com.beautyparlour.model.dto.*;
import com.beautyparlour.model.entity.User;
import com.beautyparlour.model.enums.Role;
import com.beautyparlour.repository.UserRepository;
import com.beautyparlour.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new AppException("Email already registered", HttpStatus.CONFLICT);
        }
        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setPhone(req.phone());
        user.setRole(Role.CUSTOMER);
        return UserResponse.from(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
        if (!user.isActive()) {
            throw new AppException("Account is deactivated", HttpStatus.UNAUTHORIZED);
        }

        String role = user.getRole().name();
        return new LoginResponse(
                jwtUtil.generateAccessToken(user.getEmail(), role),
                jwtUtil.generateRefreshToken(user.getEmail(), role),
                UserResponse.from(user)
        );
    }

    public String refresh(RefreshTokenRequest req) {
        if (!jwtUtil.isTokenValid(req.refreshToken())) {
            throw new AppException("Refresh token invalid or expired", HttpStatus.UNAUTHORIZED);
        }
        String email = jwtUtil.extractEmail(req.refreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.UNAUTHORIZED));
        return jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
    }
}
