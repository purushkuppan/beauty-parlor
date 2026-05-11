# ADR-003: Java 21 Feature Adoption

## Date
2026-05-10

## Status
Accepted

## Context
The backend targets Java 21 (LTS). Several Java 21 features reduce boilerplate, improve type safety, and enable the JVM to handle higher concurrency without thread-pool tuning. This ADR records which features were adopted and why.

---

## Decisions

### 1. Virtual Threads (JEP 444)
```yaml
spring.threads.virtual.enabled: true
```
**Why:** Replaces the bounded Tomcat thread pool with virtual threads. Each HTTP request runs on its own lightweight virtual thread. No executor tuning required; throughput scales with blocking I/O naturally.

**Impact:** Zero code change to application logic; one config line. Applies to all servlet handling including JPA/JDBC calls.

---

### 2. Sealed Classes + Pattern Matching for Switch (JEP 409 + JEP 441)
`AppException` is a `sealed abstract class` with five `final` nested subclasses:

| Subclass | HTTP Status |
|---|---|
| `AppException.NotFound` | 404 |
| `AppException.Conflict` | 409 |
| `AppException.Unauthorized` | 401 |
| `AppException.Forbidden` | 403 |
| `AppException.BadRequest` | 400 |

`GlobalExceptionHandler` uses an exhaustive pattern-matching switch over the sealed type:
```java
HttpStatus status = switch (ex) {
    case AppException.NotFound e     -> e.getStatus();
    case AppException.Conflict e     -> e.getStatus();
    case AppException.Unauthorized e -> e.getStatus();
    case AppException.Forbidden e    -> e.getStatus();
    case AppException.BadRequest e   -> e.getStatus();
};
```
**Why:** The compiler enforces exhaustiveness — adding a new `AppException` subtype without a matching case is a compile error. Eliminates the `HttpStatus` parameter from every throw site.

---

### 3. Exhaustive Enum Switch (JEP 441)
`AppointmentService.list()` switches on `Role` enum directly:
```java
return switch (Role.valueOf(role.replace("ROLE_", ""))) {
    case ADMIN    -> ...;
    case STAFF    -> ...;
    case CUSTOMER -> ...;
};
```
**Why:** Exhaustive — the compiler errors if a new `Role` value is added without handling it. Previously used a `String` switch with a `default` that silently fell through.

---

### 4. Switch with Multiple Labels + Status Guard (JEP 441)
`AppointmentService.cancel()` validates appointment state before cancelling:
```java
switch (appointment.getStatus()) {
    case CANCELLED -> throw new AppException.BadRequest("Appointment is already cancelled");
    case COMPLETED -> throw new AppException.BadRequest("Completed appointments cannot be cancelled");
    case PENDING, CONFIRMED -> { /* allowed */ }
}
```
**Why:** Explicit state machine — cancellable states are whitelisted, not guessed. Adding a new `AppointmentStatus` without updating this switch is a compile error.

---

### 5. Null-Case in Switch (JEP 441)
`ServiceService.listActive()` handles the optional `category` query param:
```java
return switch (category) {
    case null, "" -> serviceRepository.findByIsActiveTrue()...;
    default       -> serviceRepository.findByCategoryAndIsActiveTrue(...)...;
};
```
**Why:** Replaces a `null`-check `if/else` chain. Null is handled in the switch itself; no NPE risk.

---

### 6. Records for DTOs and Error Responses (JEP 395)
All request/response DTOs are Java records:
- `RegisterRequest`, `LoginRequest`, `LoginResponse`, `RefreshTokenRequest`
- `UserResponse`, `ServiceRequest`, `ServiceResponse`
- `AppointmentRequest`, `AppointmentResponse`
- `ErrorResponse`, `ValidationErrorResponse`

**Why:** Records are immutable by default, auto-generate canonical constructor, `equals`, `hashCode`, and `toString`. Zero Lombok required for DTOs.

**JPA entities (`User`, `Service`, `Appointment`) remain mutable classes** — records cannot be JPA entities because:
- JPA requires a no-arg constructor (records have only the canonical constructor)
- Hibernate populates `@GeneratedValue` IDs and `@CreationTimestamp` fields post-construction
- Lazy-loading proxies extend the entity class; records are implicitly `final`

---

### 7. LongStream.iterate for Slot Generation (JEP 431 / Stream API)
Replaces the mutable `while` loop in `AppointmentService.getAvailableSlots()`:
```java
// Before: mutable slot variable, ArrayList accumulation
// After: purely functional pipeline
final var allSlots = LongStream.iterate(0L, i -> i + durationMins)
        .mapToObj(workStart::plusMinutes)
        .takeWhile(s -> !s.plusMinutes(durationMins).isAfter(workEnd))
        .toList();
```
**Why:** No mutable state; `takeWhile` terminates the infinite stream at the correct boundary. `.toList()` produces an unmodifiable list.

---

### 8. SequencedCollection (JEP 431)
`DataInitializer` uses `getFirst()` and `getLast()` on `List` (which extends `SequencedCollection` in Java 21):
```java
final var priya  = staff.getFirst();
final var meena  = staff.getLast();
final var riya   = customers.getFirst();
final var pooja  = customers.getLast();
```
**Why:** More expressive than `.get(0)` / `.get(size - 1)`; intent is clear.

---

### 9. `final` on All Local Variables and Parameters
Every local variable and method parameter in non-entity code is declared `final` (or `final var`).

**Why:** Prevents accidental reassignment; communicates intent; enables the compiler to catch logic errors where a variable is unexpectedly overwritten.

**Exception:** Loop iteration variables that must be reassigned remain non-final.

---

## Consequences
- Positive: Compile-time exhaustiveness on all critical switches; zero runtime surprises when new enum values or exception types are added.
- Positive: Virtual threads remove the need for reactive programming for I/O concurrency.
- Positive: Immutable DTO records remove the need for defensive copies.
- Negative: Pattern-matching switch syntax is Java 21+ only; the codebase cannot be compiled on earlier JDKs.
- Risk: H2 in-memory database may behave differently from PostgreSQL under virtual threads — validate with PostgreSQL before production.
