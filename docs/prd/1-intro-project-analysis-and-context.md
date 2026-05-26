# 1. Intro Project Analysis and Context

## 1.1 Existing Project Overview

**Analysis Source:** IDE-based fresh analysis.

**Current Project State:**  
`gen-e2-brownfield-java-new` is a Spring Boot REST API (Java) for a car insurance demo. It exposes CRUD endpoints for Customers (`/customers`), Cars (`/cars`), and an insurance premium calculation endpoint (`POST /calculate-insurance`). Data is persisted via an in-memory H2 database using Spring Data JPA repositories. A single-page HTML frontend (`insurance-client.html`) provides a tabbed UI for managing customers, cars, and calculating premiums. The project uses Swagger/OpenAPI (springdoc) for API documentation.

## 1.2 Available Documentation

- [x] Source Tree / Architecture — inferred from IDE project structure
- [x] API Documentation — Swagger annotations present on all controllers and models
- [ ] Tech Stack Documentation (formal doc)
- [ ] Coding Standards (formal doc)
- [ ] UX/UI Guidelines (formal doc)

## 1.3 Enhancement Scope Definition

**Enhancement Type:** New Feature Addition

**Enhancement Description:**  
Add a premium calculation history feature. On the backend, every time a premium is calculated, the result (inputs + output + timestamp) will be stored in an in-memory list and exposed via a new `GET /insurance-history` endpoint. On the frontend, a new section in the Insurance Calculator tab will fetch and display this history table below the calculator form.

**Impact Assessment:** ✅ Minimal Impact (isolated additions — no existing code is modified except to wire the history store into the existing calculation flow)

## 1.4 Goals

- Provide users with a visible record of all premiums calculated during the current server session.
- Expose the history via a clean REST endpoint that follows existing API conventions.
- Display the history in the frontend without altering the existing calculator UI.

## 1.5 Background Context

The insurance demo currently calculates premiums on demand but discards the results after returning them to the caller (the H2-persisted `InsuranceCalculation` entity does exist, but users have no way to query past calculations from the UI). This feature closes that gap with the simplest possible approach: an in-memory list on the backend and a read-only table on the frontend.

No database schema changes are required; the history is intentionally ephemeral (resets on server restart), keeping the implementation lightweight and appropriate for a demo application.

---
