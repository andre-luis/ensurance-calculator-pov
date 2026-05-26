# 5. Component Architecture

## 5.1 New Components

### PremiumHistoryEntry _(Model)_

**Responsibility:** Data carrier for a single history record. Plain POJO with four fields and standard getters/setters (no Lombok, matching existing convention).  
**Integration Points:** Instantiated inside `InsuranceCalculationService.calculateInsurance()` and stored via `PremiumHistoryService`.  
**Dependencies — Existing:** None.  
**Dependencies — New:** Used by `PremiumHistoryService`.  
**Technology:** Plain Java class, `java.time.LocalDateTime`.

---

### PremiumHistoryService _(Service)_

**Responsibility:** Owns and manages the in-memory history store. Exposes `record(PremiumHistoryEntry)` and `List<PremiumHistoryEntry> getHistory()`.  
**Integration Points:** Injected into `InsuranceCalculationService`; queried by `InsuranceHistoryController`.

**Key Interfaces:**

- `void record(PremiumHistoryEntry entry)` — prepends entry to list (newest-first semantics via `add(0, entry)` or post-hoc `Collections.reverse`).
- `List<PremiumHistoryEntry> getHistory()` — returns unmodifiable view of the list, newest-first.

**Dependencies — Existing:** None.  
**Dependencies — New:** `PremiumHistoryEntry`.  
**Technology:** Spring `@Service`, `java.util.concurrent.CopyOnWriteArrayList` (thread-safe, NFR2).

---

### InsuranceHistoryController _(Controller)_

**Responsibility:** Exposes `GET /insurance-history`; returns the full history list as a JSON array.  
**Integration Points:** Delegates to `PremiumHistoryService.getHistory()`. Registered under existing `CorsFilter` automatically.

**Key Interfaces:**

- `GET /insurance-history` → `ResponseEntity<List<PremiumHistoryEntry>>` (HTTP 200, always — empty array when no history).

**Dependencies — Existing:** None (no coupling to existing controllers).  
**Dependencies — New:** `PremiumHistoryService`.  
**Technology:** Spring `@RestController`, springdoc-openapi annotations.

---

## 5.2 Modified Components

### InsuranceCalculationService _(existing — minimal modification)_

**Change:** Add `@Autowired PremiumHistoryService premiumHistoryService;` field and a single `premiumHistoryService.record(...)` call after `insuranceCalculationRepository.save(...)` in `calculateInsurance()`.

**Existing return value and exception behaviour are unchanged.**

## 5.3 Component Interaction Diagram

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
