# REQ-002: Appointment Booking

## Status
Approved

## Summary
Customers can book appointments for specific services with a chosen staff member and time slot.

## Requirements

### Functional
- [ ] Customer selects a service, staff member, date, and available time slot.
- [ ] System prevents double-booking the same staff member at the same time.
- [ ] Confirmation shown on screen after booking.
- [ ] Customer and staff both receive an email confirmation.
- [ ] Customer can cancel their appointment (up to 24 hours before).
- [ ] Admin can view, reschedule, or cancel any appointment.

### Non-Functional
- [ ] Availability check must be real-time (no stale slots).
- [ ] Booking flow completes in < 3s under normal load.

## Out of Scope
- Payment at booking time (future phase).
- SMS notifications (future phase).
