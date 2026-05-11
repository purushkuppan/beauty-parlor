# ADR-001: Full-Stack Technology Choices

## Date
2026-05-10

## Status
Accepted

## Context
Need a full-stack framework for a beauty parlour booking website with a public catalog, appointment booking, and an admin panel.

## Decision
- **Frontend:** Angular (latest stable) — TypeScript-first, strong structure for multi-page apps.
- **Backend:** Java 21 + Spring Boot 3.x — LTS release, virtual threads (Project Loom) for high concurrency.
- **Database:** PostgreSQL — relational model fits appointment/booking domain well.
- **Auth:** JWT (RS256) via Spring Security + Spring Authorization Server.
- **ORM:** Spring Data JPA (Hibernate).
- **API style:** REST JSON.

## Project Layout
```
beauty-parlour/
├── frontend/    ← Angular app
└── backend/     ← Spring Boot app
```

## Consequences
- Positive: Type safety end-to-end; Spring Boot ecosystem is mature for auth/JPA/REST.
- Negative: Two separate runtimes to manage locally (Angular dev server + Spring Boot).
- Risk: CORS must be configured carefully between Angular (port 4200) and Spring Boot (port 8080).
