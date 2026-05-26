# 2. Requirements

## 2.1 Functional Requirements

- **FR1:** The system shall maintain an in-memory ordered list of all insurance premium calculations performed since the last server start, including timestamp, customer ID, car ID, and calculated monthly premium.
- **FR2:** The system shall expose a `GET /insurance-history` REST endpoint that returns the full in-memory history list as a JSON array, ordered from most-recent to oldest.
- **FR3:** The frontend Insurance Calculator tab shall display a "Premium History" section below the calculator form that renders the history list as a table with columns: Timestamp, Customer ID, Car ID, and Monthly Premium.
- **FR4:** The frontend shall fetch the history from `GET /insurance-history` automatically after each successful premium calculation and shall provide a manual "Refresh History" button.
- **FR5:** The existing `POST /calculate-insurance` behavior shall remain unchanged; the history side-effect must not alter the response contract.

## 2.2 Non-Functional Requirements

- **NFR1:** The history endpoint must respond within the same latency profile as existing endpoints (no external I/O introduced).
- **NFR2:** The in-memory history store shall be thread-safe (use `CopyOnWriteArrayList` or `Collections.synchronizedList`).
- **NFR3:** No new external libraries may be introduced; implementation must use only what is already on the classpath.
- **NFR4:** The frontend addition must be contained within the existing `insurance-client.html` file and match the current visual style (same fonts, colors, card patterns).

## 2.3 Compatibility Requirements

- **CR1:** The `POST /calculate-insurance` request/response contract must not change.
- **CR2:** No existing H2 schema or JPA entity changes are permitted.
- **CR3:** New UI elements must use the existing CSS class patterns and color palette already defined in `insurance-client.html`.
- **CR4:** The new endpoint must be covered by the existing CORS configuration (`CorsFilter`) without requiring changes.

---
