package com.beautyparlour.controller;

import com.beautyparlour.model.dto.RegisterRequest;
import com.beautyparlour.model.dto.UserResponse;
import com.beautyparlour.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public List<UserResponse> list() {
        return staffService.listActiveStaff();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse add(@Valid @RequestBody RegisterRequest req) {
        return staffService.addStaff(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void remove(@PathVariable UUID id) {
        staffService.deactivateStaff(id);
    }
}
