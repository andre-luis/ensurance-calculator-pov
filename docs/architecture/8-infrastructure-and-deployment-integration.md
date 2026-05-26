# 8. Infrastructure and Deployment Integration

## 8.1 Existing Infrastructure

**Current Deployment:** Local Maven run (`mvn spring-boot:run`); embedded Tomcat; H2 in-memory database resets on each run.  
**Infrastructure Tools:** Maven only; no Docker, CI/CD pipeline, or cloud infrastructure present.  
**Environments:** Single local development environment.

## 8.2 Enhancement Deployment Strategy

**Deployment Approach:** No change to deployment mechanism. Build and run commands remain identical: `mvn clean package` / `mvn spring-boot:run`.  
**Infrastructure Changes:** None.  
**Pipeline Integration:** N/A — no existing pipeline.

## 8.3 Rollback Strategy

**Rollback Method:** Git revert of the feature branch. The three new files (`PremiumHistoryEntry.java`, `PremiumHistoryService.java`, `InsuranceHistoryController.java`) and the two modified files (`InsuranceCalculationService.java`, `insurance-client.html`) represent the entire changeset.  
**Risk Mitigation:** Changes are strictly additive. The single modification to `InsuranceCalculationService` is a one-line `record()` call; reverting it restores original behaviour with zero impact.  
**Monitoring:** Manual testing via Swagger UI (`/swagger-ui.html`) and `insurance-client.html` is sufficient for demo scope.

---
