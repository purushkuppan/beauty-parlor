package com.beautyparlour.service;

import com.beautyparlour.exception.AppException;
import com.beautyparlour.model.dto.*;
import com.beautyparlour.model.entity.User;
import com.beautyparlour.model.enums.Role;
import com.beautyparlour.repository.UserRepository;
import com.beautyparlour.security.JwtUtil;
import lombok.RequiredArgsConstructor;
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
    public UserResponse register(final RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new AppException.Conflict("Email already registered");
        }
        final var user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setPhone(req.phone());
        user.setRole(Role.CUSTOMER);
        return UserResponse.from(userRepository.save(user));
    }

    public LoginResponse login(final LoginRequest req) {
        final var user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new AppException.Unauthorized("Invalid email or password"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new AppException.Unauthorized("Invalid email or password");
        }
        if (!user.isActive()) {
            throw new AppException.Unauthorized("Account is deactivated");
        }

        final var role = user.getRole().name();
        return new LoginResponse(
                jwtUtil.generateAccessToken(user.getEmail(), role),
                jwtUtil.generateRefreshToken(user.getEmail(), role),
                UserResponse.from(user)
        );
    }

    public String refresh(final RefreshTokenRequest req) {
        if (!jwtUtil.isTokenValid(req.refreshToken())) {
            throw new AppException.Unauthorized("Refresh token invalid or expired");
        }
        final var email = jwtUtil.extractEmail(req.refreshToken());
        final var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException.Unauthorized("User not found"));
        return jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
    }
}
