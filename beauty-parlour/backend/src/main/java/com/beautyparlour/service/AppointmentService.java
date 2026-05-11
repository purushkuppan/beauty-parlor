package com.beautyparlour.service;

import com.beautyparlour.exception.AppException;
import com.beautyparlour.model.dto.AppointmentRequest;
import com.beautyparlour.model.dto.AppointmentResponse;
import com.beautyparlour.model.entity.Appointment;
import com.beautyparlour.model.enums.AppointmentStatus;
import com.beautyparlour.model.enums.Role;
import com.beautyparlour.repository.AppointmentRepository;
import com.beautyparlour.repository.ServiceRepository;
import com.beautyparlour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    public List<String> getAvailableSlots(final UUID staffId, final UUID serviceId, final LocalDate date) {
        final var service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new AppException.NotFound("Service not found"));

        final long durationMins = service.getDurationMins();
        final var workStart = LocalTime.of(9, 0);
        final var workEnd   = LocalTime.of(18, 0);

        // LongStream.iterate eliminates the mutable slot variable from the while loop
        final var allSlots = LongStream.iterate(0L, i -> i + durationMins)
                .mapToObj(workStart::plusMinutes)
                .takeWhile(s -> !s.plusMinutes(durationMins).isAfter(workEnd))
                .toList();

        final var zone     = ZoneOffset.UTC;
        final var dayStart = date.atStartOfDay(zone);
        final var dayEnd   = date.atTime(LocalTime.MAX).atZone(zone);
        final var booked   = appointmentRepository.findActiveByStaffAndDateRange(staffId, dayStart, dayEnd);

        return allSlots.stream()
                .filter(s -> {
                    final var slotStart = date.atTime(s).atZone(zone);
                    final var slotEnd   = slotStart.plusMinutes(durationMins);
                    return booked.stream().noneMatch(a ->
                            a.getStartTime().isBefore(slotEnd) && a.getEndTime().isAfter(slotStart));
                })
                .map(LocalTime::toString)
                .toList();
    }

    @Transactional
    public AppointmentResponse book(final AppointmentRequest req, final String customerEmail) {
        final var customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new AppException.NotFound("Customer not found"));
        final var staff = userRepository.findById(req.staffId())
                .filter(u -> u.getRole() == Role.STAFF)
                .orElseThrow(() -> new AppException.NotFound("Staff not found"));
        final var service = serviceRepository.findById(req.serviceId())
                .filter(com.beautyparlour.model.entity.Service::isActive)
                .orElseThrow(() -> new AppException.NotFound("Service not found"));

        final var endTime = req.startTime().plusMinutes(service.getDurationMins());

        final var conflict = appointmentRepository
                .findActiveByStaffAndDateRange(staff.getId(), req.startTime().minusHours(8), endTime)
                .stream().anyMatch(a ->
                        a.getStartTime().isBefore(endTime) && a.getEndTime().isAfter(req.startTime()));
        if (conflict) {
            throw new AppException.Conflict("Staff already booked at this time");
        }

        final var appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setStaff(staff);
        appointment.setService(service);
        appointment.setStartTime(req.startTime());
        appointment.setEndTime(endTime);
        appointment.setNotes(req.notes());

        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponse> list(final String email, final String role) {
        final var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException.NotFound("User not found"));

        // Exhaustive switch on Role enum — compiler verifies all cases are covered (JEP 441)
        return switch (Role.valueOf(role.replace("ROLE_", ""))) {
            case ADMIN    -> appointmentRepository.findAll().stream()
                                .map(AppointmentResponse::from).toList();
            case STAFF    -> appointmentRepository.findByStaffId(user.getId()).stream()
                                .map(AppointmentResponse::from).toList();
            case CUSTOMER -> appointmentRepository.findByCustomerId(user.getId()).stream()
                                .map(AppointmentResponse::from).toList();
        };
    }

    @Transactional
    public AppointmentResponse cancel(final UUID id, final String email, final String role) {
        final var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException.NotFound("Appointment not found"));

        final var isAdmin = role.equals("ROLE_ADMIN");
        if (!isAdmin && !appointment.getCustomer().getEmail().equals(email)) {
            throw new AppException.Forbidden("Forbidden");
        }

        // Switch with multiple labels validates cancellable state (JEP 441)
        switch (appointment.getStatus()) {
            case CANCELLED -> throw new AppException.BadRequest("Appointment is already cancelled");
            case COMPLETED -> throw new AppException.BadRequest("Completed appointments cannot be cancelled");
            case PENDING, CONFIRMED -> { /* allowed to cancel */ }
        }

        final var cutoff = ZonedDateTime.now(ZoneOffset.UTC).plusHours(24);
        if (appointment.getStartTime().isBefore(cutoff)) {
            throw new AppException.BadRequest("Cannot cancel within 24 hours of appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }
}
