package com.beautyparlour.model.dto;

import com.beautyparlour.model.enums.ServiceCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ServiceRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        @NotNull ServiceCategory category,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotNull @Min(15) @Max(480) Integer durationMins
) {}
