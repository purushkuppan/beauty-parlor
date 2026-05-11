# REQ-004: Authentication & Roles

## Status
Approved

## Summary
Users authenticate with email/password. Three roles exist: Customer, Staff, Admin.

## Requirements

### Functional
- [ ] Customers can self-register with name, email, password, phone.
- [ ] Login returns a JWT (access token, 1h) and a refresh token (7d).
- [ ] Roles: CUSTOMER, STAFF, ADMIN.
- [ ] Admin is created via a seeded database record (no public signup for admin).
- [ ] Password must be at least 8 characters with one number.
- [ ] Forgot password flow via email OTP.

### Non-Functional
- [ ] Passwords stored as bcrypt hashes (cost factor 12).
- [ ] JWTs signed with RS256.
