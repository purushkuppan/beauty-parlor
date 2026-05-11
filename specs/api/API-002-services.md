# API-002: Services

## Status
Implemented

## Standard Error Response
All error responses return an `ErrorResponse` record:
```json
{ "timestamp": "ISO-8601 string", "status": 404, "error": "message" }
```

## Related Requirement
[REQ-001](../requirements/REQ-001-services.md)

## Base Path
`/api/v1/services`

---

### GET /services
List all active services (public).

**Auth:** None

**Query Parameters**
| Name     | Type   | Required | Description                          |
|----------|--------|----------|--------------------------------------|
| category | string | No       | Filter: HAIR \| SKIN \| NAILS \| MAKEUP |

**Success — 200 OK**
```json
[
  {
    "id": "uuid",
    "name": "string",
    "description": "string",
    "category": "HAIR",
    "price": 25.00,
    "durationMins": 60
  }
]
```

---

### POST /services
Create a service (Admin only).

**Auth:** Bearer — ADMIN role required

**Request Body**
```json
{
  "name": "string (required)",
  "description": "string (optional)",
  "category": "HAIR|SKIN|NAILS|MAKEUP (required)",
  "price": "number (required, > 0)",
  "durationMins": "number (required, 15–480)"
}
```

**Success — 201 Created** — returns full service object.

---

### PUT /services/{id}
Update a service (Admin only).

**Auth:** Bearer — ADMIN role required

**Request Body** — same shape as POST, all fields optional.

**Success — 200 OK** — returns updated service object.

**Errors**
| Status | When |
|--------|------|
| 404 | Service not found |

---

### DELETE /services/{id}
Deactivate a service (Admin only, soft delete).

**Auth:** Bearer — ADMIN role required

**Success — 204 No Content**
