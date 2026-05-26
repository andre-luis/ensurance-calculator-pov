# 4. Data Models

## 4.1 New Data Models

### PremiumHistoryEntry

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

## 4.2 Schema Integration Strategy

**Database Changes Required:**

- **New Tables:** None
- **Modified Tables:** None
- **New Indexes:** None
- **Migration Strategy:** N/A — no schema changes

**Backward Compatibility:**

- Existing `InsuranceCalculation` JPA entity is untouched.
- H2 schema is unchanged; no Flyway/Liquibase scripts needed.

---
