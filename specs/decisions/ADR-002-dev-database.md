# ADR-002: H2 In-Memory Database for Development

## Date
2026-05-10

## Status
Accepted

## Context
ADR-001 selected PostgreSQL as the production database. Running a local PostgreSQL instance adds setup friction for development and testing.

## Decision
Use **H2 in-memory database** for the `dev` Spring Boot profile. PostgreSQL remains the target for staging and production.

`DataInitializer` seeds the H2 database on startup with:
- 1 admin, 3 staff, 4 customers
- 16 services across HAIR / SKIN / NAILS / MAKEUP categories (all prices in USD)
- 10 sample appointments (past and future, various statuses)

## Consequences
- Positive: Zero-install local development; database resets cleanly on each restart.
- Negative: H2 SQL dialect differences may mask PostgreSQL-specific issues (e.g. UUID handling, JSON types).
- Risk: Must regression-test against PostgreSQL before any production deployment.
- Mitigation: Keep JPA entities dialect-neutral; avoid H2-only SQL constructs in repositories.
