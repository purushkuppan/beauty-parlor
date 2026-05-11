# MODEL-001: [Model Name]

## Status
Draft | Review | Approved | Implemented

## Related Requirement
[REQ-001](../requirements/REQ-001-template.md)

## Description
What this entity represents in the domain.

## Schema

| Field       | Type      | Required | Constraints            | Description              |
|-------------|-----------|----------|------------------------|--------------------------|
| id          | UUID      | Yes      | auto-generated         | Primary key              |
| name        | string    | Yes      | max 255 chars          | Display name             |
| status      | enum      | Yes      | active \| inactive     | Current status           |
| createdAt   | datetime  | Yes      | auto-set on insert     | Creation timestamp (UTC) |
| updatedAt   | datetime  | Yes      | auto-set on update     | Last update timestamp    |

## Relationships
- **has many** [OtherModel](./MODEL-002-template.md) via `foreignKey`

## Indexes
- Primary: `id`
- Unique: `name`
- Search: `status, createdAt`

## Business Rules
- `status` defaults to `active` on creation.
- Records are soft-deleted (set `status = inactive`), never hard-deleted.
