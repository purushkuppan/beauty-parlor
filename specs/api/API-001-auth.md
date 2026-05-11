# API-001: Authentication

## Status
Approved

## Related Requirement
[REQ-004](../requirements/REQ-004-auth.md)

## Base Path
`/api/v1/auth`

---

### POST /register
Register a new customer account.

**Auth:** None

**Request Body**
```json
{
  "name": "string (required, max 100)",
  "email": "string (required, valid email)",
  "password": "string (required, min 8 chars, 1 number)",
  "phone": "string (optional)"
}
```

**Success — 201 Created**
```json
{ "id": "uuid", "name": "string", "email": "string", "role": "CUSTOMER" }
```

**Errors**
| Status | When |
|--------|------|
| 400 | Validation failure |
| 409 | Email already registered |

---

### POST /login
Authenticate and receive tokens.

**Auth:** None

**Request Body**
```json
{ "email": "string", "password": "string" }
```

**Success — 200 OK**
```json
{
  "accessToken": "JWT string (1h)",
  "refreshToken": "string (7d)",
  "user": { "id": "uuid", "name": "string", "role": "CUSTOMER|STAFF|ADMIN" }
}
```

**Errors**
| Status | When |
|--------|------|
| 401 | Invalid email or password |

---

### POST /refresh
Exchange a refresh token for a new access token.

**Auth:** None

**Request Body**
```json
{ "refreshToken": "string" }
```

**Success — 200 OK**
```json
{ "accessToken": "string" }
```

**Errors**
| Status | When |
|--------|------|
| 401 | Refresh token invalid or expired |
