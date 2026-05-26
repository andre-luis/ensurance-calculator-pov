# 7. Source Tree

## 7.1 Existing Project Structure (Relevant Excerpt)

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

## 7.2 New File Organization

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

## 7.3 Integration Guidelines

- **File Naming:** PascalCase class names; file names match class names exactly — consistent with all existing files.
- **Folder Organization:** New files placed in existing `model/`, `service/`, and `controller/` packages — no new packages introduced.
- **Import/Export Patterns:** Same `com.insurance.demo.*` package imports; no module-info changes.

---
