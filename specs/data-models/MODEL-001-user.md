# MODEL-001: User

## Status
Implemented

## Related Requirement
[REQ-004](../requirements/REQ-004-auth.md)

## Schema

| Field          | Type      | Required | Constraints                    | Description               |
|----------------|-----------|----------|--------------------------------|---------------------------|
| id             | UUID      | Yes      | auto-generated                 | Primary key               |
| name           | string    | Yes      | max 100 chars                  | Full name                 |
| email          | string    | Yes      | unique, valid email format     | Login identifier          |
| passwordHash   | string    | Yes      | bcrypt, cost 12                | Hashed password           |
| phone          | string    | No       | max 20 chars                   | Contact number            |
| role           | enum      | Yes      | CUSTOMER \| STAFF \| ADMIN     | Access level              |
| isActive       | boolean   | Yes      | default true                   | Soft delete flag          |
| createdAt      | datetime  | Yes      | auto-set                       | UTC timestamp             |
| updatedAt      | datetime  | Yes      | auto-set                       | UTC timestamp             |

## Relationships
- **has many** Appointment (as customer)
- **has many** Appointment (as staff)

## Business Rules
- Email must be unique across all roles.
- Deactivating a user (isActive=false) does not cancel future appointments — admin must handle those separately.
