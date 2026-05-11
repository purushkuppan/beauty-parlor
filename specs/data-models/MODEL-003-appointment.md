# MODEL-003: Appointment

## Status
Approved

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
- Status transitions: PENDING → CONFIRMED → COMPLETED; any state → CANCELLED.
