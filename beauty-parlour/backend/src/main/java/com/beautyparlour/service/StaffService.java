package com.beautyparlour.service;

import com.beautyparlour.exception.AppException;
import com.beautyparlour.model.dto.RegisterRequest;
import com.beautyparlour.model.dto.UserResponse;
import com.beautyparlour.model.entity.User;
import com.beautyparlour.model.enums.Role;
import com.beautyparlour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> listActiveStaff() {
        return userRepository.findByRoleAndIsActiveTrue(Role.STAFF)
                .stream().map(UserResponse::from).toList();
    }

    public UserResponse addStaff(final RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new AppException.Conflict("Email already in use");
        }
        final var staff = new User();
        staff.setName(req.name());
        staff.setEmail(req.email());
        staff.setPhone(req.phone());
        staff.setPasswordHash(passwordEncoder.encode(req.password()));
        staff.setRole(Role.STAFF);
        return UserResponse.from(userRepository.save(staff));
    }

    public void deactivateStaff(final UUID id) {
        final var staff = userRepository.findById(id)
                .filter(u -> u.getRole() == Role.STAFF)
                .orElseThrow(() -> new AppException.NotFound("Staff member not found"));
        staff.setActive(false);
        userRepository.save(staff);
    }
}
