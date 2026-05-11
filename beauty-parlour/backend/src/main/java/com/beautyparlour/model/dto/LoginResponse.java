package com.beautyparlour.model.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) {}
