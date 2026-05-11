# BEH-001: [Business Rule or Behavior Name]

## Status
Draft | Review | Approved | Implemented

## Related Requirement
[REQ-001](../requirements/REQ-001-template.md)

## Summary
What business rule or flow this spec describes.

## Scenarios

### Scenario 1: Happy path
- **Given** the system is in state X
- **When** event Y occurs
- **Then** the system should do Z

### Scenario 2: Edge case
- **Given** the system is in state X
- **When** event Y occurs with condition C
- **Then** the system should respond with error E

### Scenario 3: Failure path
- **Given** a dependency is unavailable
- **When** the operation is attempted
- **Then** the system should fail gracefully and return error code 503

## Constraints
- Must complete within 200ms at p99.
- Must be idempotent (safe to retry).

## Notes
Any additional context, links to external docs, or references to past incidents.
