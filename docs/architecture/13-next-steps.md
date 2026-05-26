# 13. Next Steps

## 13.1 Story Manager Handoff

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

## 13.2 Developer Handoff

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
