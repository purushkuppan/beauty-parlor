package com.beautyparlour.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;
import java.util.UUID;

public record AppointmentRequest(
        @NotNull UUID staffId,
        @NotNull UUID serviceId,
        @NotNull @Future ZonedDateTime startTime,
        @Size(max = 300) String notes
) {}
