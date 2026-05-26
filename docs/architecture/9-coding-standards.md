# 9. Coding Standards

## 9.1 Existing Standards Compliance

**Code Style:** Standard Java conventions; PascalCase for classes, camelCase for methods/fields. No Lombok. Plain getters/setters.  
**Linting Rules:** No checkstyle or linting config present in `pom.xml`; follow existing file formatting (4-space indent inferred from source).  
**Testing Patterns:** JUnit-based controller tests in `src/test/java/.../controller/`; `@SpringBootTest` or `@WebMvcTest` (verify by examining existing test files).  
**Documentation Style:** Swagger `@Tag`, `@Operation`, `@ApiResponse`, `@Schema` annotations on all public controller methods and model classes.

## 9.2 Enhancement-Specific Standards

- **Thread Safety:** Use `CopyOnWriteArrayList` for the history store — satisfies NFR2 without introducing locking complexity.
- **No JPA on new model:** `PremiumHistoryEntry` must NOT be annotated with `@Entity`; it is a plain POJO.
- **Swagger completeness:** `InsuranceHistoryController` must carry the same level of Swagger annotations as `InsuranceCalculationController`.

## 9.3 Critical Integration Rules

- **Existing API Compatibility:** `POST /calculate-insurance` return type, HTTP status, and response body must not change. The `record()` side-effect must be unconditional but must not throw or swallow exceptions that alter the calculation response.
- **Database Integration:** No `@Entity`, `@Repository`, or `@Transactional` annotations on new components.
- **Error Handling:** `GET /insurance-history` always returns HTTP 200; an empty list is a valid success response, not a 404.
- **Logging Consistency:** Add `@Slf4j`-equivalent logging only if existing services use it; otherwise, omit (none observed in existing service files).

---
