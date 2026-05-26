# 3. Tech Stack

## 3.1 Existing Technology Stack (Maintained As-Is)

| Category | Technology | Version | Usage in Enhancement | Notes |
|---|---|---|---|---|
| Language | Java | 11+ | New service, model, controller | No language-level changes |
| Framework | Spring Boot / Spring MVC | 2.x | `@Service`, `@RestController`, `@Autowired` | Same annotation patterns |
| Persistence | Spring Data JPA + H2 | — | NOT used by new components | History is in-memory only |
| API Docs | springdoc-openapi | — | `@Tag`, `@Operation`, `@ApiResponse` on new controller | Matches existing style |
| Frontend | Vanilla HTML/CSS/JS | — | `fetch()`, DOM manipulation added to existing file | No new libraries |
| Build | Maven | — | No `pom.xml` changes required | NFR3 enforced |

## 3.2 New Technology Additions

None. All requirements are satisfied with existing classpath dependencies.

---
