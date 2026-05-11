package com.beautyparlour.model.dto;

import com.beautyparlour.model.entity.Appointment;

import java.time.ZonedDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        String status,
        ZonedDateTime startTime,
        ZonedDateTime endTime,
        String notes,
        ServiceResponse service,
        UserResponse staff,
        UserResponse customer
) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getStatus().name(),
                a.getStartTime(),
                a.getEndTime(),
                a.getNotes(),
                ServiceResponse.from(a.getService()),
                UserResponse.from(a.getStaff()),
                UserResponse.from(a.getCustomer())
        );
    }
}
