# REQ-004: Authentication & Roles

## Status
Implemented

## Summary
Users authenticate with email/password. Three roles exist: Customer, Staff, Admin.

## Requirements

### Functional
- [x] Customers can self-register with name, email, password, phone.
- [x] Login returns a JWT (access token, 1h).
- [x] Roles: CUSTOMER, STAFF, ADMIN.
- [x] Admin is created via a seeded database record (no public signup for admin).
- [x] Password must be at least 8 characters.
- [ ] Forgot password flow via email OTP — **not yet implemented**.
- [ ] Refresh token (7d) — **not yet implemented**; access token is the only token issued.

### Non-Functional
- [x] Passwords stored as bcrypt hashes.
- [ ] JWTs signed with RS256 — **implementation uses HS256**; RS256 deferred to production hardening.

## Implementation Notes
- JWT secret is configured in `application.properties` via `jwt.secret`.
- Token expiry: 1 hour (configured via `jwt.expiration`).
