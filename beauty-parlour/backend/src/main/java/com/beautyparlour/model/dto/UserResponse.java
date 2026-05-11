package com.beautyparlour.model.dto;

import com.beautyparlour.model.entity.User;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email, String role) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole().name());
    }
}
