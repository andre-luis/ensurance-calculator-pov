# 6. Epic 1: Premium Calculation History

**Epic Goal:** Enable users to view a running history of all insurance premiums calculated during the current server session, via a new backend endpoint and a new frontend history table.

**Integration Requirements:** The new backend service must be injected into the existing `InsuranceCalculationService` without altering its public API. The frontend must call the new endpoint only after a confirmed successful calculation response.

---

## Story 1.1 — Backend: In-Memory Premium History API

**As a** developer consuming the insurance API,  
**I want** a `GET /insurance-history` endpoint that returns all past premium calculations (timestamp, customer ID, car ID, monthly premium),  
**so that** clients can retrieve the full session history of calculated premiums.

### Acceptance Criteria

1. A new `PremiumHistoryEntry` class exists in the `com.insurance.demo.model` package with fields: `timestamp` (ISO-8601 string or `LocalDateTime`), `customerId` (Long), `carId` (Long), `monthlyPremium` (double).
2. A new `PremiumHistoryService` Spring `@Service` exists with methods `record(PremiumHistoryEntry)` and `List<PremiumHistoryEntry> getHistory()` returning entries newest-first.
3. The history store uses a thread-safe collection (e.g., `CopyOnWriteArrayList`).
4. `InsuranceCalculationService.calculateInsurance()` calls `PremiumHistoryService.record(...)` with correct values after each successful calculation; the existing return value and behavior are unchanged.
5. A new `InsuranceHistoryController` at `GET /insurance-history` returns HTTP 200 with a JSON array of `PremiumHistoryEntry` objects.
6. The endpoint is documented with Swagger `@Tag`, `@Operation`, and `@ApiResponse` annotations consistent with the existing controllers.
7. A new unit test verifies that calling `calculateInsurance()` twice results in `getHistory()` returning two entries in newest-first order.
8. `GET /insurance-history` returns an empty JSON array `[]` when no calculations have been performed.

### Integration Verification

- **IV1:** `POST /calculate-insurance` still returns the same response body and HTTP status as before this story.
- **IV2:** Existing `InsuranceCalculationControllerTest` tests pass without modification.
- **IV3:** No new Maven dependencies are added to `pom.xml`.

---

## Story 1.2 — Frontend: Premium History Display

**As a** user of the Insurance Management System UI,  
**I want** to see a table of past premium calculations below the calculator form,  
**so that** I can review all premiums computed during my session without leaving the page.

### Acceptance Criteria

1. A "Premium History" card section is added below the existing calculator form inside the Insurance Calculator tab in `insurance-client.html`.
2. The history table has four columns: **Timestamp**, **Customer ID**, **Car ID**, **Monthly Premium (R$)**.
3. After a successful premium calculation response, the frontend automatically fetches `GET /insurance-history` and re-renders the history table.
4. A "Refresh History" button is present in the history card and triggers the same fetch-and-render on click.
5. When the history list is empty, the table area displays the message "No premium history yet."
6. The new card and table visually match the existing UI style (same fonts, color palette, border-radius, and box-shadow).
7. No new JavaScript libraries are introduced; implementation uses only `fetch` and vanilla DOM manipulation.
8. The history section is visible only when the Insurance Calculator tab is active.

### Integration Verification

- **IV1:** All existing tabs (Customers, Cars, Insurance Calculator) continue to function correctly after the HTML changes.
- **IV2:** The calculator form submission and result display work exactly as before.
- **IV3:** The page renders without JavaScript console errors when the backend is not running (graceful empty state on fetch failure).
