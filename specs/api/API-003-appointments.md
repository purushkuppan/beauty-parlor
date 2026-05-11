# API-003: Appointments

## Status
Implemented

## Related Requirement
[REQ-002](../requirements/REQ-002-appointments.md)

## Base Path
`/api/v1/appointments`

---

### GET /appointments/availability
Get available time slots for a staff member on a given date.

**Auth:** None

**Query Parameters**
| Name      | Type   | Required | Description          |
|-----------|--------|----------|----------------------|
| staffId   | uuid   | Yes      | Staff member ID      |
| serviceId | uuid   | Yes      | Determines slot size |
| date      | string | Yes      | ISO 8601 date (YYYY-MM-DD) |

**Success — 200 OK**
```json
["09:00", "10:00", "11:30"]
```

> **Note:** Response is a plain JSON array of `HH:mm` strings, not a wrapped object. The original spec had `{ "availableSlots": [...] }` but the implementation returns a flat array — this spec was corrected to match.

---

### POST /appointments
Book an appointment (Customer or Admin).

**Auth:** Bearer — CUSTOMER or ADMIN

**Request Body**
```json
{
  "staffId": "uuid",
  "serviceId": "uuid",
  "startTime": "ISO 8601 datetime (UTC)",
  "notes": "string (optional)"
}
```

**Success — 201 Created**
```json
{
  "id": "uuid",
  "status": "PENDING",
  "startTime": "ISO 8601",
  "endTime": "ISO 8601",
  "service": { "name": "string", "price": 25.00 },
  "staff": { "name": "string" }
}
```

**Errors**
| Status | When |
|--------|------|
| 400 | Invalid time slot or past date |
| 409 | Staff already booked at this time |

---

### GET /appointments
List appointments (filtered by role).

**Auth:** Bearer — any authenticated user

**Behavior by role:**
- CUSTOMER → their own appointments only
- STAFF → their assigned appointments
- ADMIN → all appointments

**Query Parameters**
| Name   | Type   | Required | Description                      |
|--------|--------|----------|----------------------------------|
| status | string | No       | PENDING\|CONFIRMED\|CANCELLED\|COMPLETED |
| from   | date   | No       | Start of date range              |
| to     | date   | No       | End of date range                |

**Success — 200 OK** — array of appointment objects.

---

### PATCH /appointments/{id}/cancel
Cancel an appointment.

**Auth:** Bearer — CUSTOMER (own only) or ADMIN

**Constraints:** `startTime` must be > 24h from now.

**Success — 200 OK** — returns updated appointment with `status: CANCELLED`.

**Errors**
| Status | When |
|--------|------|
| 400 | Cancellation window passed (< 24h) |
| 403 | Customer trying to cancel someone else's appointment |
| 404 | Appointment not found |
