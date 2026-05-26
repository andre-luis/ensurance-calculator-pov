# 1. Introduction

This document outlines the architectural approach for enhancing `gen-e2-brownfield-java-new` with a **Premium Calculation History** feature. Its primary goal is to serve as the guiding architectural blueprint for AI-driven development of new features while ensuring seamless integration with the existing system.

**Relationship to Existing Architecture:**
This document supplements the existing Spring Boot project architecture by defining how the new in-memory history store and history endpoint integrate with the current calculation flow. Where conflicts arise between new and existing patterns, this document provides guidance on maintaining consistency while implementing enhancements. No existing APIs, database schemas, or test contracts are modified.

## 1.1 Existing Project Analysis

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
