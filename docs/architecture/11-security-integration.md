# 11. Security Integration

## 11.1 Existing Security Measures

**Authentication:** None (demo application — all endpoints are open).  
**Authorization:** None.  
**Data Protection:** H2 in-memory only; no PII persisted beyond server session. The new history store also holds only in-memory data — same posture.  
**Security Tools:** `CorsFilter` restricts cross-origin access at the application level.

## 11.2 Enhancement Security Requirements

**New Security Measures:**

- No authentication/authorization changes required (consistent with existing posture).
- `PremiumHistoryEntry` data (customer IDs, car IDs, premiums) is already accessible via `GET /calculate-insurance` responses — no new data exposure.
- The in-memory list is not bounded; for production use a max-size eviction policy would be recommended, but this is out of scope for a demo.

**Integration Points:** `InsuranceHistoryController` is automatically covered by `CorsFilter` (applies to all paths) — no explicit CORS configuration needed (CR4).  
**Compliance Requirements:** N/A — demo application.

## 11.3 Security Testing

**Existing Security Tests:** None observed.  
**New Security Test Requirements:** Verify CORS headers are present on `GET /insurance-history` responses (can be confirmed manually with `curl -H "Origin: http://localhost"` or via existing `sample_curl_request.sh` pattern).  
**Penetration Testing:** Out of scope for demo.

---
