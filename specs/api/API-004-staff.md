# API-004: Staff Management

## Status
Implemented

## Related Requirement
[REQ-003](../requirements/REQ-003-staff.md)

## Base Path
`/api/v1/staff`

---

### GET /staff
List all active staff members (public).

**Auth:** None

**Success — 200 OK**
```json
[
  {
    "id": "uuid",
    "name": "string",
    "email": "string",
    "phone": "string | null",
    "role": "STAFF"
  }
]
```

---

### POST /staff
Add a new staff member (Admin only).

**Auth:** Bearer — ADMIN role required

**Request Body**
```json
{
  "name": "string (required)",
  "email": "string (required, valid email)",
  "password": "string (required, min 8 chars)",
  "phone": "string (optional)"
}
```

**Success — 201 Created**
```json
{
  "id": "uuid",
  "name": "string",
  "email": "string",
  "role": "STAFF"
}
```

**Errors**
| Status | When |
|--------|------|
| 400 | Validation failure |
| 409 | Email already registered |

---

### DELETE /staff/{id}
Deactivate a staff member (Admin only, soft delete).

**Auth:** Bearer — ADMIN role required

**Success — 204 No Content**

**Errors**
| Status | When |
|--------|------|
| 404 | Staff member not found |
| 403 | Caller is not ADMIN |
