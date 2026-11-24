This is a demo to use angular application - connecting to back-end application via gcp load balancer and then connecting a back-end app-a to connect back-end app-b . See the details below

Browser               Global External LB           GCS Bucket          GKE: app-a Pod         GKE Metadata / IAM       GKE: app-b Pod
(Angular SPA)         (34.160.81.240.nip.io)       (angular-backend)     (10.76.x.x:8080)       (Workload Identity)      (10.76.x.x:8081)
     │                          │                          │                     │                       │                       │
     │═══ PHASE 1: Load Angular SPA (Static Assets) ═══════│                     │                       │                       │
     │                          │                          │                     │                       │                       │
     │── 1. HTTPS GET / ───────▶│                          │                     │                       │                       │
     │   (TLS terminated by     │── 2. Fetch index.html ──▶│                     │                       │                       │
     │    app-ssl-cert-v2)      │◀─ 3. HTML / JS / CSS ────│                     │                       │                       │
     │◀─ 4. 200 OK (Cached) ────│                          │                     │                       │                       │
     │                          │                          │                     │                       │                       │
     │═══ PHASE 2: Click "Call Backend" (/api/aggregate) ══│                     │                       │                       │
     │                          │                          │                     │                       │                       │
     │── 5. HTTPS GET ─────────▶│                          │                     │                       │                       │
     │      /api/aggregate      │── 6. URL Map matches ───▶│────────────────────▶│                       │                       │
     │                          │      /api/* -> NEG       │  (HTTP over VPC)    │                       │                       │
     │                          │                          │                     │                       │                       │
     │                          │                          │                     │── 7. Request OIDC ───▶│                       │
     │                          │                          │                     │      ID Token         │                       │
     │                          │                          │                     │      (aud=app-b-svc)  │                       │
     │                          │                          │                     │◀─ 8. Signed Google ───│                       │
     │                          │                          │                     │      JWT (app-a-gsa)  │                       │
     │                          │                          │                     │                       │                       │
     │                          │                          │                     │── 9. HTTP GET /internal/secret-data ─────────▶│
     │                          │                          │                     │    Authorization: Bearer <OIDC_JWT>           │
     │                          │                          │                     │    (NetworkPolicy allows app=app-a only)      │
     │                          │                          │                     │                       │                       │
     │                          │                          │                     │                       │◀─ 10. Fetch JWKS & ───│
     │                          │                          │                     │                       │   verify sig/aud/email│
     │                          │                          │                     │                       │                       │
     │                          │                          │                     │◀─ 11. 200 OK JSON ────────────────────────────│
     │                          │                          │                     │   {"service":"app-b","status":"verified-oidc"}│
     │                          │◀─ 12. 200 OK JSON ─────────────────────────────│                       │                       │
     │◀─ 13. 200 OK JSON ───────│   {"gateway":"app-a","downstream":{...}}       │                       │                       │
     │    (Rendered in UI)      │                          │                     │                       │                       │
