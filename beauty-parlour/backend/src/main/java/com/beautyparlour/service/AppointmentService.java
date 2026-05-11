package com.beautyparlour.service;

import com.beautyparlour.exception.AppException;
import com.beautyparlour.model.dto.AppointmentRequest;
import com.beautyparlour.model.dto.AppointmentResponse;
import com.beautyparlour.model.entity.Appointment;
import com.beautyparlour.model.entity.User;
import com.beautyparlour.model.enums.AppointmentStatus;
import com.beautyparlour.model.enums.Role;
import com.beautyparlour.repository.AppointmentRepository;
import com.beautyparlour.repository.ServiceRepository;
import com.beautyparlour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    public List<String> getAvailableSlots(UUID staffId, UUID serviceId, LocalDate date) {
        com.beautyparlour.model.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new AppException("Service not found", HttpStatus.NOT_FOUND));

        int durationMins = service.getDurationMins();
        LocalTime workStart = LocalTime.of(9, 0);
        LocalTime workEnd = LocalTime.of(18, 0);

        List<LocalTime> allSlots = new ArrayList<>();
        LocalTime slot = workStart;
        while (!slot.plusMinutes(durationMins).isAfter(workEnd)) {
            allSlots.add(slot);
            slot = slot.plusMinutes(durationMins);
        }

        ZoneId zone = ZoneOffset.UTC;
        ZonedDateTime dayStart = date.atStartOfDay(zone);
        ZonedDateTime dayEnd = date.atTime(LocalTime.MAX).atZone(zone);
        List<Appointment> booked = appointmentRepository.findActiveByStaffAndDateRange(staffId, dayStart, dayEnd);

        return allSlots.stream()
                .filter(s -> {
                    ZonedDateTime slotStart = date.atTime(s).atZone(zone);
                    ZonedDateTime slotEnd = slotStart.plusMinutes(durationMins);
                    return booked.stream().noneMatch(a ->
                            a.getStartTime().isBefore(slotEnd) && a.getEndTime().isAfter(slotStart));
                })
                .map(LocalTime::toString)
                .toList();
    }

    @Transactional
    public AppointmentResponse book(AppointmentRequest req, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new AppException("Customer not found", HttpStatus.NOT_FOUND));
        User staff = userRepository.findById(req.staffId())
                .filter(u -> u.getRole() == Role.STAFF)
                .orElseThrow(() -> new AppException("Staff not found", HttpStatus.NOT_FOUND));
        com.beautyparlour.model.entity.Service service = serviceRepository.findById(req.serviceId())
                .filter(com.beautyparlour.model.entity.Service::isActive)
                .orElseThrow(() -> new AppException("Service not found", HttpStatus.NOT_FOUND));

        ZonedDateTime endTime = req.startTime().plusMinutes(service.getDurationMins());

        boolean conflict = appointmentRepository
                .findActiveByStaffAndDateRange(staff.getId(), req.startTime().minusHours(8), endTime)
                .stream().anyMatch(a ->
                        a.getStartTime().isBefore(endTime) && a.getEndTime().isAfter(req.startTime()));
        if (conflict) {
            throw new AppException("Staff already booked at this time", HttpStatus.CONFLICT);
        }

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setStaff(staff);
        appointment.setService(service);
        appointment.setStartTime(req.startTime());
        appointment.setEndTime(endTime);
        appointment.setNotes(req.notes());

        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponse> list(String email, String role) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        return switch (role) {
            case "ROLE_ADMIN" -> appointmentRepository.findAll().stream().map(AppointmentResponse::from).toList();
            case "ROLE_STAFF" -> appointmentRepository.findByStaffId(user.getId()).stream().map(AppointmentResponse::from).toList();
            default -> appointmentRepository.findByCustomerId(user.getId()).stream().map(AppointmentResponse::from).toList();
        };
    }

    @Transactional
    public AppointmentResponse cancel(UUID id, String email, String role) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException("Appointment not found", HttpStatus.NOT_FOUND));

        boolean isAdmin = role.equals("ROLE_ADMIN");
        if (!isAdmin && !appointment.getCustomer().getEmail().equals(email)) {
            throw new AppException("Forbidden", HttpStatus.FORBIDDEN);
        }

        ZonedDateTime cutoff = ZonedDateTime.now(ZoneOffset.UTC).plusHours(24);
        if (appointment.getStartTime().isBefore(cutoff)) {
            throw new AppException("Cannot cancel within 24 hours of appointment", HttpStatus.BAD_REQUEST);
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }
}
