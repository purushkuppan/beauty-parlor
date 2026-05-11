# MODEL-002: Service

## Status
Implemented

## Related Requirement
[REQ-001](../requirements/REQ-001-services.md)

## Schema

| Field        | Type      | Required | Constraints                              | Description              |
|--------------|-----------|----------|------------------------------------------|--------------------------|
| id           | UUID      | Yes      | auto-generated                           | Primary key              |
| name         | string    | Yes      | max 100 chars                            | Service name             |
| description  | string    | No       | max 500 chars                            | Short description        |
| category     | enum      | Yes      | HAIR \| SKIN \| NAILS \| MAKEUP          | Grouping category        |
| price        | decimal   | Yes      | positive, 2 decimal places               | Price in USD             |
| durationMins | integer   | Yes      | 15–480                                   | Duration in minutes      |
| isActive     | boolean   | Yes      | default true                             | Soft delete / visibility |
| createdAt    | datetime  | Yes      | auto-set                                 | UTC timestamp            |
| updatedAt    | datetime  | Yes      | auto-set                                 | UTC timestamp            |

## Business Rules
- Deactivating a service hides it from the public catalog but does not cancel existing appointments.

## Implementation Pattern
`Service` is a mutable JPA entity, **not** a record. `ServiceRequest` (input) and `ServiceResponse` (output) are Java records. See [ADR-003](../decisions/ADR-003-java21-features.md).
