package com.beautyparlour.model.dto;

import com.beautyparlour.model.entity.Service;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceResponse(UUID id, String name, String description, String category,
                               BigDecimal price, int durationMins) {
    public static ServiceResponse from(Service s) {
        return new ServiceResponse(s.getId(), s.getName(), s.getDescription(),
                s.getCategory().name(), s.getPrice(), s.getDurationMins());
    }
}
