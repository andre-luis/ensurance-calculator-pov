# Insurance Demo Brownfield Enhancement PRD

## Change Log

| Change | Date | Version | Description | Author |
|--------|------|---------|-------------|--------|
| Initial draft | 2026-05-26 | 1.0 | Premium history feature — backend API + frontend display | Andre (PM Agent) |

---

## 1. Intro Project Analysis and Context

### 1.1 Existing Project Overview

**Analysis Source:** IDE-based fresh analysis.

**Current Project State:**  
`gen-e2-brownfield-java-new` is a Spring Boot REST API (Java) for a car insurance demo. It exposes CRUD endpoints for Customers (`/customers`), Cars (`/cars`), and an insurance premium calculation endpoint (`POST /calculate-insurance`). Data is persisted via an in-memory H2 database using Spring Data JPA repositories. A single-page HTML frontend (`insurance-client.html`) provides a tabbed UI for managing customers, cars, and calculating premiums. The project uses Swagger/OpenAPI (springdoc) for API documentation.

### 1.2 Available Documentation

- [x] Source Tree / Architecture — inferred from IDE project structure
- [x] API Documentation — Swagger annotations present on all controllers and models
- [ ] Tech Stack Documentation (formal doc)
- [ ] Coding Standards (formal doc)
- [ ] UX/UI Guidelines (formal doc)

### 1.3 Enhancement Scope Definition

**Enhancement Type:** New Feature Addition

**Enhancement Description:**  
Add a premium calculation history feature. On the backend, every time a premium is calculated, the result (inputs + output + timestamp) will be stored in an in-memory list and exposed via a new `GET /insurance-history` endpoint. On the frontend, a new section in the Insurance Calculator tab will fetch and display this history table below the calculator form.

**Impact Assessment:** ✅ Minimal Impact (isolated additions — no existing code is modified except to wire the history store into the existing calculation flow)

### 1.4 Goals

- Provide users with a visible record of all premiums calculated during the current server session.
- Expose the history via a clean REST endpoint that follows existing API conventions.
- Display the history in the frontend without altering the existing calculator UI.

### 1.5 Background Context

The insurance demo currently calculates premiums on demand but discards the results after returning them to the caller (the H2-persisted `InsuranceCalculation` entity does exist, but users have no way to query past calculations from the UI). This feature closes that gap with the simplest possible approach: an in-memory list on the backend and a read-only table on the frontend.

No database schema changes are required; the history is intentionally ephemeral (resets on server restart), keeping the implementation lightweight and appropriate for a demo application.

---

## 2. Requirements

### 2.1 Functional Requirements

- **FR1:** The system shall maintain an in-memory ordered list of all insurance premium calculations performed since the last server start, including timestamp, customer ID, car ID, and calculated monthly premium.
- **FR2:** The system shall expose a `GET /insurance-history` REST endpoint that returns the full in-memory history list as a JSON array, ordered from most-recent to oldest.
- **FR3:** The frontend Insurance Calculator tab shall display a "Premium History" section below the calculator form that renders the history list as a table with columns: Timestamp, Customer ID, Car ID, and Monthly Premium.
- **FR4:** The frontend shall fetch the history from `GET /insurance-history` automatically after each successful premium calculation and shall provide a manual "Refresh History" button.
- **FR5:** The existing `POST /calculate-insurance` behavior shall remain unchanged; the history side-effect must not alter the response contract.

### 2.2 Non-Functional Requirements

- **NFR1:** The history endpoint must respond within the same latency profile as existing endpoints (no external I/O introduced).
- **NFR2:** The in-memory history store shall be thread-safe (use `CopyOnWriteArrayList` or `Collections.synchronizedList`).
- **NFR3:** No new external libraries may be introduced; implementation must use only what is already on the classpath.
- **NFR4:** The frontend addition must be contained within the existing `insurance-client.html` file and match the current visual style (same fonts, colors, card patterns).

### 2.3 Compatibility Requirements

- **CR1:** The `POST /calculate-insurance` request/response contract must not change.
- **CR2:** No existing H2 schema or JPA entity changes are permitted.
- **CR3:** New UI elements must use the existing CSS class patterns and color palette already defined in `insurance-client.html`.
- **CR4:** The new endpoint must be covered by the existing CORS configuration (`CorsFilter`) without requiring changes.

---

## 3. User Interface Enhancement Goals

### 3.1 Integration with Existing UI

The new "Premium History" section will be appended inside the Insurance Calculator tab content area, below the existing calculator form card. It will use the same card/container styling already present in the HTML.

### 3.2 Modified / New Screens and Views

- **Modified:** Insurance Calculator tab section within `insurance-client.html` — add history table card below the calculator form.

### 3.3 UI Consistency Requirements

- Table must use the existing `#667eea` / `#764ba2` accent colors for headers.
- Font, border-radius, and shadow styles must match existing cards.
- An empty-state message ("No history yet.") must display when the list is empty.

---

## 4. Technical Constraints and Integration Requirements

### 4.1 Existing Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java (inferred Java 11+) |
| Framework | Spring Boot, Spring MVC, Spring Data JPA |
| Database | H2 in-memory (existing) |
| API Docs | springdoc-openapi (Swagger UI) |
| Frontend | Vanilla HTML/CSS/JS (single file) |
| Build | Maven (`pom.xml`) |

### 4.2 Integration Approach

- **Backend:** A new `PremiumHistoryService` (Spring `@Service`) will hold a `CopyOnWriteArrayList<PremiumHistoryEntry>` where `PremiumHistoryEntry` is a plain Java record/class (no JPA). `InsuranceCalculationService.calculateInsurance()` will call `PremiumHistoryService.record(...)` after a successful calculation. A new `InsuranceHistoryController` (`GET /insurance-history`) will delegate to `PremiumHistoryService.getHistory()`.
- **Frontend:** A new `<section>` block added to the Insurance Calculator tab in `insurance-client.html`; history is fetched via `fetch('/insurance-history')`.
- **Testing:** Existing controller tests are not modified; new unit/integration tests cover the new endpoint.

### 4.3 Code Organization and Standards

- New files follow existing package structure under `com.insurance.demo`.
- Class names follow existing PascalCase convention.
- No Lombok; plain getters/setters as in existing model classes.
- Swagger `@Tag` and `@Operation` annotations on the new controller, matching existing style.

### 4.4 Risk Assessment

| Risk | Likelihood | Mitigation |
|------|-----------|------------|
| Thread-safety of in-memory list | Low | Use `CopyOnWriteArrayList` |
| CORS blocking new endpoint | Very Low | Existing `CorsFilter` applies to all paths |
| History lost on restart | Accepted | Intentional for demo scope |

---

## 5. Epic and Story Structure

**Epic Structure Decision:** Single epic — both stories are tightly related additions (backend store + frontend consumer) for one cohesive feature. Stories are sequenced backend-first so the frontend story can be validated against a working API.

---

## 6. Epic 1: Premium Calculation History

**Epic Goal:** Enable users to view a running history of all insurance premiums calculated during the current server session, via a new backend endpoint and a new frontend history table.

**Integration Requirements:** The new backend service must be injected into the existing `InsuranceCalculationService` without altering its public API. The frontend must call the new endpoint only after a confirmed successful calculation response.

---

### Story 1.1 — Backend: In-Memory Premium History API

**As a** developer consuming the insurance API,  
**I want** a `GET /insurance-history` endpoint that returns all past premium calculations (timestamp, customer ID, car ID, monthly premium),  
**so that** clients can retrieve the full session history of calculated premiums.

#### Acceptance Criteria

1. A new `PremiumHistoryEntry` class exists in the `com.insurance.demo.model` package with fields: `timestamp` (ISO-8601 string or `LocalDateTime`), `customerId` (Long), `carId` (Long), `monthlyPremium` (double).
2. A new `PremiumHistoryService` Spring `@Service` exists with methods `record(PremiumHistoryEntry)` and `List<PremiumHistoryEntry> getHistory()` returning entries newest-first.
3. The history store uses a thread-safe collection (e.g., `CopyOnWriteArrayList`).
4. `InsuranceCalculationService.calculateInsurance()` calls `PremiumHistoryService.record(...)` with correct values after each successful calculation; the existing return value and behavior are unchanged.
5. A new `InsuranceHistoryController` at `GET /insurance-history` returns HTTP 200 with a JSON array of `PremiumHistoryEntry` objects.
6. The endpoint is documented with Swagger `@Tag`, `@Operation`, and `@ApiResponse` annotations consistent with the existing controllers.
7. A new unit test verifies that calling `calculateInsurance()` twice results in `getHistory()` returning two entries in newest-first order.
8. `GET /insurance-history` returns an empty JSON array `[]` when no calculations have been performed.

#### Integration Verification

- **IV1:** `POST /calculate-insurance` still returns the same response body and HTTP status as before this story.
- **IV2:** Existing `InsuranceCalculationControllerTest` tests pass without modification.
- **IV3:** No new Maven dependencies are added to `pom.xml`.

---

### Story 1.2 — Frontend: Premium History Display

**As a** user of the Insurance Management System UI,  
**I want** to see a table of past premium calculations below the calculator form,  
**so that** I can review all premiums computed during my session without leaving the page.

#### Acceptance Criteria

1. A "Premium History" card section is added below the existing calculator form inside the Insurance Calculator tab in `insurance-client.html`.
2. The history table has four columns: **Timestamp**, **Customer ID**, **Car ID**, **Monthly Premium (R$)**.
3. After a successful premium calculation response, the frontend automatically fetches `GET /insurance-history` and re-renders the history table.
4. A "Refresh History" button is present in the history card and triggers the same fetch-and-render on click.
5. When the history list is empty, the table area displays the message "No premium history yet."
6. The new card and table visually match the existing UI style (same fonts, color palette, border-radius, and box-shadow).
7. No new JavaScript libraries are introduced; implementation uses only `fetch` and vanilla DOM manipulation.
8. The history section is visible only when the Insurance Calculator tab is active.

#### Integration Verification

- **IV1:** All existing tabs (Customers, Cars, Insurance Calculator) continue to function correctly after the HTML changes.
- **IV2:** The calculator form submission and result display work exactly as before.
- **IV3:** The page renders without JavaScript console errors when the backend is not running (graceful empty state on fetch failure).
