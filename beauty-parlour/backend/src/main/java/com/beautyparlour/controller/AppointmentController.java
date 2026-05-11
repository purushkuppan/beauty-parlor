package com.beautyparlour.controller;

import com.beautyparlour.model.dto.AppointmentRequest;
import com.beautyparlour.model.dto.AppointmentResponse;
import com.beautyparlour.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
            @RequestParam UUID staffId,
            @RequestParam UUID serviceId,
            @RequestParam String date) {
        return appointmentService.getAvailableSlots(staffId, serviceId, LocalDate.parse(date));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse book(@Valid @RequestBody AppointmentRequest req, Authentication auth) {
        return appointmentService.book(req, auth.getName());
    }

    @GetMapping
    public List<AppointmentResponse> list(Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        return appointmentService.list(auth.getName(), role);
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable UUID id, Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        return appointmentService.cancel(id, auth.getName(), role);
    }
}
