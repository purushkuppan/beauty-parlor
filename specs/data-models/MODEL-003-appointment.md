# MODEL-003: Appointment

## Status
Implemented

## Related Requirement
[REQ-002](../requirements/REQ-002-appointments.md)

## Schema

| Field        | Type      | Required | Constraints                                      | Description                  |
|--------------|-----------|----------|--------------------------------------------------|------------------------------|
| id           | UUID      | Yes      | auto-generated                                   | Primary key                  |
| customerId   | UUID      | Yes      | FK → User (role=CUSTOMER)                        | Who booked                   |
| staffId      | UUID      | Yes      | FK → User (role=STAFF)                           | Who performs the service      |
| serviceId    | UUID      | Yes      | FK → Service                                     | Which service                |
| startTime    | datetime  | Yes      | future datetime, UTC                             | Appointment start             |
| endTime      | datetime  | Yes      | startTime + service.durationMins                 | Calculated, not user-supplied|
| status       | enum      | Yes      | PENDING \| CONFIRMED \| CANCELLED \| COMPLETED  | Current state                |
| notes        | string    | No       | max 300 chars                                    | Customer notes               |
| createdAt    | datetime  | Yes      | auto-set                                         | UTC timestamp                |
| updatedAt    | datetime  | Yes      | auto-set                                         | UTC timestamp                |

## Relationships
- **belongs to** User (customer)
- **belongs to** User (staff)
- **belongs to** Service

## Indexes
- Compound unique: `staffId + startTime` (prevents double-booking)
- Query index: `customerId + status`
- Query index: `staffId + startTime`

## Business Rules
- `endTime` is always `startTime + service.durationMins` — computed server-side, never accepted from client.
- Cancellation only allowed when `status = PENDING | CONFIRMED` and `startTime > now + 24h`.
- Cancellation of a `CANCELLED` or `COMPLETED` appointment returns 400.
- Status transitions: PENDING → CONFIRMED → COMPLETED; PENDING | CONFIRMED → CANCELLED.

## Implementation Pattern
`Appointment` is a mutable JPA entity, **not** a record. `AppointmentRequest` and `AppointmentResponse` are Java records. The cancel business rule is enforced via an exhaustive switch on `AppointmentStatus` (sealed-compatible) — a compile error results if a new status is added without updating the guard. See [ADR-003](../decisions/ADR-003-java21-features.md).
