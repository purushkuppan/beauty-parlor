# Spec-Driven Development Workspace

## Workflow

1. **Write the spec first** — before any code.
2. **Get spec approved** — update Status to `Approved`.
3. **Implement** — code in `src/` must satisfy the spec exactly.
4. **Write tests** — derived directly from the spec scenarios.
5. **Mark implemented** — update spec Status to `Implemented`.

## Folder Map

| Folder | Purpose |
|---|---|
| `specs/requirements/` | High-level feature requirements (REQ-###) |
| `specs/api/` | Endpoint contracts — inputs, outputs, errors (API-###) |
| `specs/data-models/` | Entity schemas and relationships (MODEL-###) |
| `specs/behaviors/` | Business rules and Given/When/Then scenarios (BEH-###) |
| `specs/decisions/` | Architecture Decision Records (ADR-###) |
| `src/` | Implementation — must conform to approved specs |
| `tests/` | Tests derived from spec scenarios |

## Numbering Convention

Use zero-padded sequential IDs: `REQ-001`, `API-002`, `MODEL-003`, etc.

## Spec Status Lifecycle

`Draft` → `Review` → `Approved` → `Implemented`

Never implement a spec that is still in `Draft` or `Review`.

## Rules

- A spec file is the source of truth. If code and spec disagree, fix the code (or raise a spec change).
- API changes require updating the spec before merging.
- ADRs are append-only — deprecate or supersede, never delete.
