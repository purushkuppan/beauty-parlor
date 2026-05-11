# API-001: [Endpoint Group Name]

## Status
Draft | Review | Approved | Implemented

## Related Requirement
[REQ-001](../requirements/REQ-001-template.md)

## Base Path
`/api/v1/resource`

---

## Endpoints

### GET /resource
Fetch a list of resources.

**Auth:** Bearer token required

**Query Parameters**
| Name   | Type   | Required | Description         |
|--------|--------|----------|---------------------|
| limit  | number | No       | Max results (1-100) |
| offset | number | No       | Pagination offset   |

**Success Response — 200 OK**
```json
{
  "data": [],
  "total": 0
}
```

**Error Responses**
| Status | Code              | When                     |
|--------|-------------------|--------------------------|
| 401    | UNAUTHORIZED      | Missing or invalid token |
| 500    | INTERNAL_ERROR    | Unexpected server error  |

---

### POST /resource
Create a new resource.

**Auth:** Bearer token required

**Request Body**
```json
{
  "name": "string (required)",
  "description": "string (optional)"
}
```

**Success Response — 201 Created**
```json
{
  "id": "uuid",
  "name": "string",
  "createdAt": "ISO 8601 timestamp"
}
```

**Error Responses**
| Status | Code              | When                          |
|--------|-------------------|-------------------------------|
| 400    | VALIDATION_ERROR  | Missing or invalid fields     |
| 401    | UNAUTHORIZED      | Missing or invalid token      |
| 409    | CONFLICT          | Resource already exists       |
