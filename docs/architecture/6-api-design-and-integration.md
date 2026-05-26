# 6. API Design and Integration

## 6.1 API Integration Strategy

**API Integration Strategy:** Additive — new endpoint added under a dedicated controller; no existing endpoints are modified.  
**Authentication:** None (demo application — matches existing posture; all endpoints are open).  
**Versioning:** No versioning applied; consistent with existing unversioned endpoint style.

## 6.2 New API Endpoints

### GET /insurance-history

- **Method:** `GET`
- **Endpoint:** `/insurance-history`
- **Purpose:** Returns the full in-memory history of premium calculations, ordered newest-first.
- **Integration:** Does not interact with JPA repositories or H2. Reads directly from `PremiumHistoryService` in-memory store.

**Request:** No request body; no query parameters.

**Response (200 OK):**
```json
[
  {
    "timestamp": "2026-05-26T14:32:01",
    "customerId": 1,
    "carId": 3,
    "monthlyPremium": 245.00
  },
  {
    "timestamp": "2026-05-26T14:28:44",
    "customerId": 2,
    "carId": 1,
    "monthlyPremium": 180.00
  }
]
```

**Empty state (200 OK):** `[]`

**Swagger annotations:**
```java
@Tag(name = "Insurance History", description = "APIs for retrieving insurance calculation history")
@Operation(summary = "Get premium calculation history", description = "Returns all premiums calculated in the current server session, newest first")
@ApiResponse(responseCode = "200", description = "History retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PremiumHistoryEntry.class)))
```

---
