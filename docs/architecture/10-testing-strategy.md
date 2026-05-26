# 10. Testing Strategy

## 10.1 Integration with Existing Tests

**Existing Test Framework:** JUnit (Spring Boot test slice — inferred from `surefire-reports`).  
**Test Organisation:** One test class per controller in `src/test/java/com/insurance/demo/controller/`.  
**Coverage Requirements:** All existing tests must pass without modification (IV2 from PRD).

## 10.2 New Testing Requirements

### Unit Tests for New Components

- **Framework:** JUnit (same as existing)
- **Location:** `src/test/java/com/insurance/demo/service/PremiumHistoryServiceTest.java`
- **Coverage Target:**
  - `record()` followed by `getHistory()` returns entries newest-first.
  - Two calls to `record()` result in exactly 2 entries.
  - Empty store returns empty list.
- **Integration with Existing:** New tests are additive; Maven Surefire picks them up automatically.

### Integration Tests

- **Scope:** `InsuranceHistoryControllerTest` — `@WebMvcTest` or `@SpringBootTest` slice.
- **Existing System Verification:** `POST /calculate-insurance` → `GET /insurance-history` returns 1 entry.
- **New Feature Testing:** `GET /insurance-history` before any calculation returns `[]` with HTTP 200.

### Regression Testing

- **Existing Feature Verification:** `InsuranceCalculationControllerTest`, `CarControllerTest`, `CustomerControllerTest` must pass unchanged.
- **Automated Regression Suite:** `mvn test` — all tests in one command.
- **Manual Testing Requirements:** Smoke test via `insurance-client.html` in browser to verify history card renders and refreshes correctly.

---
