# REQ-002: Appointment Booking

## Status
Implemented

## Summary
Customers can book appointments for specific services with a chosen staff member and time slot.

## Requirements

### Functional
- [x] Customer selects a service, staff member, date, and available time slot.
- [x] System prevents double-booking the same staff member at the same time.
- [x] Confirmation shown on screen after booking.
- [ ] Customer and staff both receive an email confirmation — **not yet implemented**.
- [x] Customer can cancel their appointment (up to 24 hours before).
- [x] Admin can view and cancel any appointment.
- [ ] Admin can reschedule an appointment — **not yet implemented**.

### Non-Functional
- [x] Availability check is real-time (computed from existing appointments on each request).
- [ ] Booking flow < 3s — not formally measured; acceptable in local dev.

## Out of Scope
- Payment at booking time (future phase).
- SMS notifications (future phase).

## Implementation Notes
- Availability endpoint returns a plain `string[]` of time slots (see API-003).
- Seed data includes past (COMPLETED/CANCELLED) and future (CONFIRMED/PENDING) appointments.
