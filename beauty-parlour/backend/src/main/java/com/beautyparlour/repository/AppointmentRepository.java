package com.beautyparlour.repository;

import com.beautyparlour.model.entity.Appointment;
import com.beautyparlour.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByCustomerId(UUID customerId);
    List<Appointment> findByStaffId(UUID staffId);
    List<Appointment> findByCustomerIdAndStatus(UUID customerId, AppointmentStatus status);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.staff.id = :staffId
              AND a.startTime >= :from
              AND a.startTime < :to
              AND a.status <> 'CANCELLED'
            """)
    List<Appointment> findActiveByStaffAndDateRange(
            @Param("staffId") UUID staffId,
            @Param("from") ZonedDateTime from,
            @Param("to") ZonedDateTime to
    );

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.startTime >= :from AND a.startTime <= :to
            ORDER BY a.startTime ASC
            """)
    List<Appointment> findAllInRange(
            @Param("from") ZonedDateTime from,
            @Param("to") ZonedDateTime to
    );
}
