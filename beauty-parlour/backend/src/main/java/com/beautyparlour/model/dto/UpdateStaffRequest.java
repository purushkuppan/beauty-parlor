package com.beautyparlour.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateStaffRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email String email,
        @Size(max = 20) String phone
) {}
