# REQ-003: Staff Profiles

## Status
Implemented

## Summary
Display staff members with their specializations so customers can choose who to book with.

## Requirements

### Functional
- [x] Staff list is publicly accessible (name, email, phone returned).
- [x] Admin can add and deactivate staff from the frontend admin panel.
- [x] Admin can create staff via API (POST /api/v1/staff).
- [x] Staff can log in and view their own appointment schedule.
- [ ] Each staff profile shows photo, bio, specializations, working hours — **not yet implemented**.
- [ ] Admin can update existing staff profile details — **not yet implemented**.

### Non-Functional
- [ ] Staff profile photos stored in server filesystem or cloud storage — **deferred**.

## Implementation Notes
- Staff are Users with `role = STAFF`; created by Admin only (no public signup).
- Deactivating a staff member sets `isActive = false`; existing appointments are not auto-cancelled.
