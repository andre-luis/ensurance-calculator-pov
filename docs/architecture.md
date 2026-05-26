# Insurance Demo Brownfield Enhancement Architecture

## Change Log

| Change | Date | Version | Description | Author |
|--------|------|---------|-------------|--------|
| Initial draft | 2026-05-26 | 1.0 | Premium history feature — backend API + frontend display | Mauro (Architect Agent) |

---

## 1. Introduction

This document outlines the architectural approach for enhancing `gen-e2-brownfield-java-new` with a **Premium Calculation History** feature. Its primary goal is to serve as the guiding architectural blueprint for AI-driven development of new features while ensuring seamless integration with the existing system.

**Relationship to Existing Architecture:**
This document supplements the existing Spring Boot project architecture by defining how the new in-memory history store and history endpoint integrate with the current calculation flow. Where conflicts arise between new and existing patterns, this document provides guidance on maintaining consistency while implementing enhancements. No existing APIs, database schemas, or test contracts are modified.

### 1.1 Existing Project Analysis

**Current Project State:**

- **Primary Purpose:** Car insurance premium calculator demo — REST API with a single-page HTML frontend.
- **Current Tech Stack:** Java (Spring Boot, Spring MVC, Spring Data JPA), H2 in-memory database, springdoc-openapi (Swagger), Vanilla HTML/CSS/JS, Maven build.
- **Architecture Style:** Layered MVC — Controller → Service → Repository → JPA Entity → H2.
- **Deployment Method:** Local Maven run (`mvn spring-boot:run`); no containerisation or CI/CD pipeline present.

**Available Documentation:**

- `docs/prd.md` — Completed PRD v1.0 for the premium history enhancement.
- Swagger/OpenAPI annotations on all controllers and models (self-documenting API).
- `insurance-client.html` — Single-page frontend; serves as living UI spec.
- Source tree inferred from IDE structure (no formal architecture doc existed prior to this document).

**Identified Constraints:**

- No new Maven dependencies may be introduced (NFR3).
- No changes to existing H2 schema or JPA entities (CR2).
- `POST /calculate-insurance` request/response contract must remain unchanged (CR1, FR5).
- History is intentionally ephemeral (resets on server restart) — appropriate for demo scope.
- Existing `CorsFilter` applies globally; no CORS changes required.

---

## 2. Enhancement Scope and Integration Strategy

### 2.1 Enhancement Overview

**Enhancement Type:** New Feature Addition  
**Scope:** In-memory premium calculation history — backend API + frontend display  
**Integration Impact:** Minimal — isolated additions; existing code touched only to wire the history call into `InsuranceCalculationService.calculateInsurance()`

### 2.2 Integration Approach

**Code Integration Strategy:** Additive injection — `PremiumHistoryService` is injected into `InsuranceCalculationService` via `@Autowired`; a single `record(...)` call is appended after the existing `save()` call. No existing logic is altered.

**Database Integration:** None. `PremiumHistoryEntry` is a plain Java class (not a JPA entity). No new tables, columns, or migrations.

**API Integration:** New `GET /insurance-history` endpoint under a new `InsuranceHistoryController`. Follows identical patterns to existing controllers (`@RestController`, `@RequestMapping`, Swagger annotations).

**UI Integration:** New `<section>` card appended inside the existing Insurance Calculator tab in `insurance-client.html`. Uses existing CSS classes and color palette; no new stylesheets.

### 2.3 Compatibility Requirements

| Requirement | Approach |
|---|---|
| Existing API Compatibility | `POST /calculate-insurance` contract unchanged; `PremiumHistoryService.record()` is a void side-effect |
| Database Schema Compatibility | No JPA entities or H2 schema changes |
| UI/UX Consistency | New card uses existing `#667eea`/`#764ba2` palette, same `border-radius`, `box-shadow`, and font patterns |
| Performance Impact | `CopyOnWriteArrayList` add is O(n) copy; acceptable for demo scale with no external I/O |

---

## 3. Tech Stack

### 3.1 Existing Technology Stack (Maintained As-Is)

| Category | Technology | Version | Usage in Enhancement | Notes |
|---|---|---|---|---|
| Language | Java | 11+ | New service, model, controller | No language-level changes |
| Framework | Spring Boot / Spring MVC | 2.x | `@Service`, `@RestController`, `@Autowired` | Same annotation patterns |
| Persistence | Spring Data JPA + H2 | — | NOT used by new components | History is in-memory only |
| API Docs | springdoc-openapi | — | `@Tag`, `@Operation`, `@ApiResponse` on new controller | Matches existing style |
| Frontend | Vanilla HTML/CSS/JS | — | `fetch()`, DOM manipulation added to existing file | No new libraries |
| Build | Maven | — | No `pom.xml` changes required | NFR3 enforced |

### 3.2 New Technology Additions

None. All requirements are satisfied with existing classpath dependencies.

---

## 4. Data Models

### 4.1 New Data Models

#### PremiumHistoryEntry

**Purpose:** Represents a single premium calculation event recorded in the in-memory history store.  
**Integration:** Plain Java class (no JPA). Lives in `com.insurance.demo.model`. Not persisted to H2.

**Key Attributes:**

| Attribute | Type | Description |
|---|---|---|
| `timestamp` | `java.time.LocalDateTime` | When the calculation occurred |
| `customerId` | `Long` | Foreign reference to the Customer (ID only) |
| `carId` | `Long` | Foreign reference to the Car (ID only) |
| `monthlyPremium` | `double` | Calculated monthly premium amount |

**Relationships:**

- **With Existing:** Logical references to `Customer` and `Car` by ID — no JPA `@ManyToOne` relationship; IDs are extracted from the saved `InsuranceCalculation`.
- **With New:** Held in `PremiumHistoryService`'s `CopyOnWriteArrayList<PremiumHistoryEntry>`.

### 4.2 Schema Integration Strategy

**Database Changes Required:**

- **New Tables:** None
- **Modified Tables:** None
- **New Indexes:** None
- **Migration Strategy:** N/A — no schema changes

**Backward Compatibility:**

- Existing `InsuranceCalculation` JPA entity is untouched.
- H2 schema is unchanged; no Flyway/Liquibase scripts needed.

---

## 5. Component Architecture

### 5.1 New Components

#### PremiumHistoryEntry _(Model)_

**Responsibility:** Data carrier for a single history record. Plain POJO with four fields and standard getters/setters (no Lombok, matching existing convention).  
**Integration Points:** Instantiated inside `InsuranceCalculationService.calculateInsurance()` and stored via `PremiumHistoryService`.  
**Dependencies — Existing:** None.  
**Dependencies — New:** Used by `PremiumHistoryService`.  
**Technology:** Plain Java class, `java.time.LocalDateTime`.

---

#### PremiumHistoryService _(Service)_

**Responsibility:** Owns and manages the in-memory history store. Exposes `record(PremiumHistoryEntry)` and `List<PremiumHistoryEntry> getHistory()`.  
**Integration Points:** Injected into `InsuranceCalculationService`; queried by `InsuranceHistoryController`.

**Key Interfaces:**

- `void record(PremiumHistoryEntry entry)` — prepends entry to list (newest-first semantics via `add(0, entry)` or post-hoc `Collections.reverse`).
- `List<PremiumHistoryEntry> getHistory()` — returns unmodifiable view of the list, newest-first.

**Dependencies — Existing:** None.  
**Dependencies — New:** `PremiumHistoryEntry`.  
**Technology:** Spring `@Service`, `java.util.concurrent.CopyOnWriteArrayList` (thread-safe, NFR2).

---

#### InsuranceHistoryController _(Controller)_

**Responsibility:** Exposes `GET /insurance-history`; returns the full history list as a JSON array.  
**Integration Points:** Delegates to `PremiumHistoryService.getHistory()`. Registered under existing `CorsFilter` automatically.

**Key Interfaces:**

- `GET /insurance-history` → `ResponseEntity<List<PremiumHistoryEntry>>` (HTTP 200, always — empty array when no history).

**Dependencies — Existing:** None (no coupling to existing controllers).  
**Dependencies — New:** `PremiumHistoryService`.  
**Technology:** Spring `@RestController`, springdoc-openapi annotations.

---

### 5.2 Modified Components

#### InsuranceCalculationService _(existing — minimal modification)_

**Change:** Add `@Autowired PremiumHistoryService premiumHistoryService;` field and a single `premiumHistoryService.record(...)` call after `insuranceCalculationRepository.save(...)` in `calculateInsurance()`.

**Existing return value and exception behaviour are unchanged.**

### 5.3 Component Interaction Diagram

```mermaid
graph TD
    Client([HTTP Client / Browser])

    subgraph Existing
        ICC[InsuranceCalculationController<br/>POST /calculate-insurance]
        ICS[InsuranceCalculationService]
        ICR[InsuranceCalculationRepository]
        H2[(H2 Database)]
    end

    subgraph New
        IHC[InsuranceHistoryController<br/>GET /insurance-history]
        PHS[PremiumHistoryService<br/>CopyOnWriteArrayList]
        PHE[PremiumHistoryEntry]
    end

    Client -->|POST /calculate-insurance| ICC
    ICC --> ICS
    ICS --> ICR
    ICR --> H2
    ICS -->|record(entry)| PHS
    PHS -->|stores| PHE

    Client -->|GET /insurance-history| IHC
    IHC -->|getHistory()| PHS
    PHS -->|List of entries| IHC
    IHC -->|JSON array| Client
```

---

## 6. API Design and Integration

### 6.1 API Integration Strategy

**API Integration Strategy:** Additive — new endpoint added under a dedicated controller; no existing endpoints are modified.  
**Authentication:** None (demo application — matches existing posture; all endpoints are open).  
**Versioning:** No versioning applied; consistent with existing unversioned endpoint style.

### 6.2 New API Endpoints

#### GET /insurance-history

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

## 7. Source Tree

### 7.1 Existing Project Structure (Relevant Excerpt)

```plaintext
src/main/java/com/insurance/demo/
├── controller/
│   ├── CarController.java
│   ├── CustomerController.java
│   └── InsuranceCalculationController.java
├── model/
│   ├── Car.java
│   ├── Customer.java
│   └── InsuranceCalculation.java
├── service/
│   ├── CarService.java
│   ├── CustomerService.java
│   └── InsuranceCalculationService.java
├── repository/  (unchanged)
├── dto/         (unchanged)
└── exception/   (unchanged)

src/test/java/com/insurance/demo/
└── controller/
    ├── CarControllerTest.java
    ├── CustomerControllerTest.java
    └── InsuranceCalculationControllerTest.java

insurance-client.html  (single-page frontend)
```

### 7.2 New File Organization

```plaintext
src/main/java/com/insurance/demo/
├── controller/
│   ├── InsuranceCalculationController.java  # Existing — unchanged
│   └── InsuranceHistoryController.java      # NEW
├── model/
│   ├── InsuranceCalculation.java            # Existing — unchanged
│   └── PremiumHistoryEntry.java             # NEW
└── service/
    ├── InsuranceCalculationService.java     # MODIFIED — add record() call
    └── PremiumHistoryService.java           # NEW

src/test/java/com/insurance/demo/
├── controller/
│   ├── InsuranceCalculationControllerTest.java  # Existing — unchanged
│   └── InsuranceHistoryControllerTest.java      # NEW
└── service/
    └── PremiumHistoryServiceTest.java           # NEW

insurance-client.html  # MODIFIED — history section appended to calculator tab
```

### 7.3 Integration Guidelines

- **File Naming:** PascalCase class names; file names match class names exactly — consistent with all existing files.
- **Folder Organization:** New files placed in existing `model/`, `service/`, and `controller/` packages — no new packages introduced.
- **Import/Export Patterns:** Same `com.insurance.demo.*` package imports; no module-info changes.

---

## 8. Infrastructure and Deployment Integration

### 8.1 Existing Infrastructure

**Current Deployment:** Local Maven run (`mvn spring-boot:run`); embedded Tomcat; H2 in-memory database resets on each run.  
**Infrastructure Tools:** Maven only; no Docker, CI/CD pipeline, or cloud infrastructure present.  
**Environments:** Single local development environment.

### 8.2 Enhancement Deployment Strategy

**Deployment Approach:** No change to deployment mechanism. Build and run commands remain identical: `mvn clean package` / `mvn spring-boot:run`.  
**Infrastructure Changes:** None.  
**Pipeline Integration:** N/A — no existing pipeline.

### 8.3 Rollback Strategy

**Rollback Method:** Git revert of the feature branch. The three new files (`PremiumHistoryEntry.java`, `PremiumHistoryService.java`, `InsuranceHistoryController.java`) and the two modified files (`InsuranceCalculationService.java`, `insurance-client.html`) represent the entire changeset.  
**Risk Mitigation:** Changes are strictly additive. The single modification to `InsuranceCalculationService` is a one-line `record()` call; reverting it restores original behaviour with zero impact.  
**Monitoring:** Manual testing via Swagger UI (`/swagger-ui.html`) and `insurance-client.html` is sufficient for demo scope.

---

## 9. Coding Standards

### 9.1 Existing Standards Compliance

**Code Style:** Standard Java conventions; PascalCase for classes, camelCase for methods/fields. No Lombok. Plain getters/setters.  
**Linting Rules:** No checkstyle or linting config present in `pom.xml`; follow existing file formatting (4-space indent inferred from source).  
**Testing Patterns:** JUnit-based controller tests in `src/test/java/.../controller/`; `@SpringBootTest` or `@WebMvcTest` (verify by examining existing test files).  
**Documentation Style:** Swagger `@Tag`, `@Operation`, `@ApiResponse`, `@Schema` annotations on all public controller methods and model classes.

### 9.2 Enhancement-Specific Standards

- **Thread Safety:** Use `CopyOnWriteArrayList` for the history store — satisfies NFR2 without introducing locking complexity.
- **No JPA on new model:** `PremiumHistoryEntry` must NOT be annotated with `@Entity`; it is a plain POJO.
- **Swagger completeness:** `InsuranceHistoryController` must carry the same level of Swagger annotations as `InsuranceCalculationController`.

### 9.3 Critical Integration Rules

- **Existing API Compatibility:** `POST /calculate-insurance` return type, HTTP status, and response body must not change. The `record()` side-effect must be unconditional but must not throw or swallow exceptions that alter the calculation response.
- **Database Integration:** No `@Entity`, `@Repository`, or `@Transactional` annotations on new components.
- **Error Handling:** `GET /insurance-history` always returns HTTP 200; an empty list is a valid success response, not a 404.
- **Logging Consistency:** Add `@Slf4j`-equivalent logging only if existing services use it; otherwise, omit (none observed in existing service files).

---

## 10. Testing Strategy

### 10.1 Integration with Existing Tests

**Existing Test Framework:** JUnit (Spring Boot test slice — inferred from `surefire-reports`).  
**Test Organisation:** One test class per controller in `src/test/java/com/insurance/demo/controller/`.  
**Coverage Requirements:** All existing tests must pass without modification (IV2 from PRD).

### 10.2 New Testing Requirements

#### Unit Tests for New Components

- **Framework:** JUnit (same as existing)
- **Location:** `src/test/java/com/insurance/demo/service/PremiumHistoryServiceTest.java`
- **Coverage Target:**
  - `record()` followed by `getHistory()` returns entries newest-first.
  - Two calls to `record()` result in exactly 2 entries.
  - Empty store returns empty list.
- **Integration with Existing:** New tests are additive; Maven Surefire picks them up automatically.

#### Integration Tests

- **Scope:** `InsuranceHistoryControllerTest` — `@WebMvcTest` or `@SpringBootTest` slice.
- **Existing System Verification:** `POST /calculate-insurance` → `GET /insurance-history` returns 1 entry.
- **New Feature Testing:** `GET /insurance-history` before any calculation returns `[]` with HTTP 200.

#### Regression Testing

- **Existing Feature Verification:** `InsuranceCalculationControllerTest`, `CarControllerTest`, `CustomerControllerTest` must pass unchanged.
- **Automated Regression Suite:** `mvn test` — all tests in one command.
- **Manual Testing Requirements:** Smoke test via `insurance-client.html` in browser to verify history card renders and refreshes correctly.

---

## 11. Security Integration

### 11.1 Existing Security Measures

**Authentication:** None (demo application — all endpoints are open).  
**Authorization:** None.  
**Data Protection:** H2 in-memory only; no PII persisted beyond server session. The new history store also holds only in-memory data — same posture.  
**Security Tools:** `CorsFilter` restricts cross-origin access at the application level.

### 11.2 Enhancement Security Requirements

**New Security Measures:**

- No authentication/authorization changes required (consistent with existing posture).
- `PremiumHistoryEntry` data (customer IDs, car IDs, premiums) is already accessible via `GET /calculate-insurance` responses — no new data exposure.
- The in-memory list is not bounded; for production use a max-size eviction policy would be recommended, but this is out of scope for a demo.

**Integration Points:** `InsuranceHistoryController` is automatically covered by `CorsFilter` (applies to all paths) — no explicit CORS configuration needed (CR4).  
**Compliance Requirements:** N/A — demo application.

### 11.3 Security Testing

**Existing Security Tests:** None observed.  
**New Security Test Requirements:** Verify CORS headers are present on `GET /insurance-history` responses (can be confirmed manually with `curl -H "Origin: http://localhost"` or via existing `sample_curl_request.sh` pattern).  
**Penetration Testing:** Out of scope for demo.

---

## 12. Checklist Results

| Area | Check | Status | Notes |
|---|---|---|---|
| Integration | No existing APIs broken | ✅ | `POST /calculate-insurance` contract unchanged |
| Integration | No DB schema changes | ✅ | `PremiumHistoryEntry` is not a JPA entity |
| Integration | CORS covers new endpoint | ✅ | `CorsFilter` applies globally |
| Tech Stack | No new dependencies | ✅ | `CopyOnWriteArrayList` is in JDK |
| Architecture | Follows existing layered pattern | ✅ | Controller → Service → (no repo) |
| Architecture | Thread-safe data store | ✅ | `CopyOnWriteArrayList` |
| Testing | Existing tests unmodified | ✅ | New test classes only |
| Testing | New components covered | ✅ | Service unit test + controller integration test |
| Frontend | Uses existing CSS patterns | ✅ | Same card, colors, fonts |
| Frontend | No new JS libraries | ✅ | `fetch` + vanilla DOM only |
| Security | No new data exposure beyond existing | ✅ | Data already available via existing endpoints |

---

## 13. Next Steps

### 13.1 Story Manager Handoff

> **To Story Manager Agent:**
>
> The brownfield architecture for the **Premium Calculation History** feature is complete. Reference `docs/architecture.md` (this document) and `docs/prd.md` for full context.
>
> **Key integration requirements:**
> - `PremiumHistoryService` must be `@Autowired` into `InsuranceCalculationService` — this is the only existing file with a functional change.
> - `InsuranceHistoryController` at `GET /insurance-history` must be a standalone controller; no changes to `InsuranceCalculationController`.
> - No `pom.xml` changes. No JPA entities.
>
> **Story sequence (backend-first):**
> 1. **Story 1.1** — Backend: `PremiumHistoryEntry` model, `PremiumHistoryService`, `InsuranceHistoryController`, wire into `InsuranceCalculationService`, unit + integration tests.
> 2. **Story 1.2** — Frontend: History card in `insurance-client.html`, auto-fetch after calculation, Refresh button, empty state message.
>
> Story 1.2 must not begin until Story 1.1's `GET /insurance-history` endpoint is verified working.

### 13.2 Developer Handoff

> **To Developer Agent:**
>
> Architecture doc: `docs/architecture.md`. PRD: `docs/prd.md`.
>
> **Implementation sequence:**
> 1. Create `PremiumHistoryEntry.java` in `com.insurance.demo.model` — plain POJO, no JPA, four fields with standard getters/setters.
> 2. Create `PremiumHistoryService.java` in `com.insurance.demo.service` — `@Service`, `CopyOnWriteArrayList`, `record()` and `getHistory()` methods.
> 3. Modify `InsuranceCalculationService.java` — add `@Autowired PremiumHistoryService` and call `premiumHistoryService.record(new PremiumHistoryEntry(...))` immediately after `insuranceCalculationRepository.save(insuranceCalculation)`. Return value unchanged.
> 4. Create `InsuranceHistoryController.java` in `com.insurance.demo.controller` — `GET /insurance-history`, full Swagger annotations, always returns HTTP 200.
> 5. Add history card section to `insurance-client.html` inside the Insurance Calculator tab — table with four columns, auto-fetch on calculation success, Refresh button, empty-state message, matching existing CSS style.
> 6. Write `PremiumHistoryServiceTest.java` and `InsuranceHistoryControllerTest.java`.
> 7. Run `mvn test` — all tests (old + new) must pass.
>
> **Compatibility checks before merging:**
> - `POST /calculate-insurance` still returns the same response (existing `InsuranceCalculationControllerTest` passes).
> - `GET /insurance-history` returns `[]` when called before any calculation.
> - No new entries in `pom.xml`.
