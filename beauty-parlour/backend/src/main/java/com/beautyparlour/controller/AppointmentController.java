package com.beautyparlour.controller;

import com.beautyparlour.model.dto.AppointmentRequest;
import com.beautyparlour.model.dto.AppointmentResponse;
import com.beautyparlour.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/availability")
    public List<String> availability(
            @RequestParam final UUID staffId,
            @RequestParam final UUID serviceId,
            @RequestParam final String date) {
        return appointmentService.getAvailableSlots(staffId, serviceId, LocalDate.parse(date));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse book(@Valid @RequestBody final AppointmentRequest req, final Authentication auth) {
        return appointmentService.book(req, auth.getName());
    }

    @GetMapping
    public List<AppointmentResponse> list(final Authentication auth) {
        final var role = auth.getAuthorities().stream()
                .findFirst().map(GrantedAuthority::getAuthority).orElseThrow();
        return appointmentService.list(auth.getName(), role);
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable final UUID id, final Authentication auth) {
        final var role = auth.getAuthorities().stream()
                .findFirst().map(GrantedAuthority::getAuthority).orElseThrow();
        return appointmentService.cancel(id, auth.getName(), role);
    }
}
