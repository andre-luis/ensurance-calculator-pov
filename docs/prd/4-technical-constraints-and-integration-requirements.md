# 4. Technical Constraints and Integration Requirements

## 4.1 Existing Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java (inferred Java 11+) |
| Framework | Spring Boot, Spring MVC, Spring Data JPA |
| Database | H2 in-memory (existing) |
| API Docs | springdoc-openapi (Swagger UI) |
| Frontend | Vanilla HTML/CSS/JS (single file) |
| Build | Maven (`pom.xml`) |

## 4.2 Integration Approach

- **Backend:** A new `PremiumHistoryService` (Spring `@Service`) will hold a `CopyOnWriteArrayList<PremiumHistoryEntry>` where `PremiumHistoryEntry` is a plain Java record/class (no JPA). `InsuranceCalculationService.calculateInsurance()` will call `PremiumHistoryService.record(...)` after a successful calculation. A new `InsuranceHistoryController` (`GET /insurance-history`) will delegate to `PremiumHistoryService.getHistory()`.
- **Frontend:** A new `<section>` block added to the Insurance Calculator tab in `insurance-client.html`; history is fetched via `fetch('/insurance-history')`.
- **Testing:** Existing controller tests are not modified; new unit/integration tests cover the new endpoint.

## 4.3 Code Organization and Standards

- New files follow existing package structure under `com.insurance.demo`.
- Class names follow existing PascalCase convention.
- No Lombok; plain getters/setters as in existing model classes.
- Swagger `@Tag` and `@Operation` annotations on the new controller, matching existing style.

## 4.4 Risk Assessment

| Risk | Likelihood | Mitigation |
|------|-----------|------------|
| Thread-safety of in-memory list | Low | Use `CopyOnWriteArrayList` |
| CORS blocking new endpoint | Very Low | Existing `CorsFilter` applies to all paths |
| History lost on restart | Accepted | Intentional for demo scope |

---
