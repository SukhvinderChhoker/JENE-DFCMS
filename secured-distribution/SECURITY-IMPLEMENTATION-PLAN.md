# JENE-DFCMS Security Architecture & Implementation Plan

## Table of Contents
1. [Executive Summary](#1-executive-summary)
2. [Threat Model](#2-threat-model)
3. [License System Architecture](#3-license-system-architecture)
4. [Docker Security Hardening](#4-docker-security-hardening)
5. [Source Code Protection](#5-source-code-protection)
6. [Container Hardening](#6-container-hardening)
7. [Deployment Workflow](#7-deployment-workflow)
8. [Risk Analysis](#8-risk-analysis)
9. [Limitations & Realistic Expectations](#9-limitations--realistic-expectations)

---

## 1. Executive Summary

This document describes the security architecture for producing a **protected distributable version** of JENE-DFCMS. The approach combines:

- **RSA-2048 license validation** (offline, cryptographic)
- **Production-hardened Docker images** (non-root, read-only, minimal)
- **Source code obfuscation** (Angular AOT + minification, ProGuard for Java)
- **Container security** (no-new-privileges, read-only FS, resource limits)

**Key principle:** No Docker image can be 100% copy-proof. The goal is to make unauthorized use **practically difficult** and **economically unviable** for attackers.

---

## 2. Threat Model

| Threat Actor | Capability | Risk Level |
|-------------|-----------|------------|
| Casual user | Runs Docker image, inspects logs | LOW |
| Curious developer | Examines frontend JS, API calls | MEDIUM |
| Competitor | Decompiles Java JAR, extracts logic | HIGH |
| Malicious actor | Modifies container, bypasses checks | CRITICAL |

**Defenses applied per layer:**

| Layer | Protection |
|-------|-----------|
| License | RSA-signed tokens, expiry, tamper detection |
| Frontend | AOT compilation, minification, no source maps |
| Backend | JAR packaging (not source), ProGuard obfuscation |
| Container | Non-root, read-only FS, no shell access |
| Network | Rate limiting, security headers, CORS |
| Data | Encrypted secrets, env-var driven config |

---

## 3. License System Architecture

### 3.1 RSA Key Pair Model

```
+------------------+          +------------------+
|  PRIVATE KEY     |          |  PUBLIC KEY      |
|  (Owner Only)    |          |  (Embedded in JAR)|
|  Signs licenses  |          |  Verifies licenses|
+------------------+          +------------------+
        |                              |
        v                              v
+------------------+          +------------------+
| License Generator |          | License Validator |
| Python script     |          | Spring component  |
| Offline tool      |          | Runs at startup   |
+------------------+          +------------------+
```

### 3.2 License File Format

```json
{
  "license": {
    "licenseId": "A1B2C3D4-E5F6-7890-ABCD-EF1234567890",
    "appName": "JENE-DFCMS",
    "version": "1.0",
    "customerName": "Customer Name",
    "licenseType": "trial|annual",
    "issueDate": "2026-06-29T00:00:00Z",
    "expiryDate": "2026-07-19T00:00:00Z",
    "hardwareId": "a1b2c3d4e5f67890",
    "maxUsers": 5,
    "features": ["cases", "evidence", "tasks", "reports", "quiz"]
  },
  "signature": "<base64 RSA-SHA256 signature>"
}
```

### 3.3 Validation Flow

```
Application Start
       |
       v
LicenseRunListener.environmentPrepared()
       |
       v
LicenseValidator.validateLicense()
       |
       +-- Load public key from classpath
       +-- Load license file from disk
       +-- Verify RSA signature (tamper detection)
       +-- Check expiry date
       +-- Validate app name
       +-- Check issue date
       |
       +-- VALID --> Application starts normally
       |
       +-- INVALID --> throw RuntimeException
                        Application refuses to start
                        HTTP 503 for all requests
```

### 3.4 License Types

| Type | Duration | Max Users | Use Case |
|------|----------|-----------|----------|
| Trial | 20 days | 5 | Evaluation, demo |
| Annual | 365 days | 50 | Production deployment |

### 3.5 Security Properties

- **RSA-2048 SHA256:** Cannot forge without private key
- **Offline validation:** No network dependency
- **Tamper detection:** Any modification invalidates signature
- **Expiry enforcement:** Expired licenses rejected
- **App binding:** License tied to "JENE-DFCMS" only
- **Hardware fingerprinting:** Optional device binding

---

## 4. Docker Security Hardening

### 4.1 Backend Dockerfile Security

| Feature | Implementation |
|---------|---------------|
| Non-root user | `jene:jene` user, UID-based |
| Minimal base | `eclipse-temurin:17-jre-jammy` (~200MB) |
| Multi-stage | Build artifacts not in runtime image |
| Read-only FS | `read_only: true` in compose |
| No new privileges | `security_opt: no-new-privileges:true` |
| Resource limits | `-XX:MaxRAMPercentage=75.0` |
| Health check | Built-in health endpoint |
| Clean apt | `rm -rf /var/lib/apt/lists/*` |

### 4.2 Frontend Dockerfile Security

| Feature | Implementation |
|---------|---------------|
| Non-root user | `nginx` user (built-in) |
| Minimal base | `nginx:alpine` (~40MB) |
| Multi-stage | Node.js not in runtime image |
| Read-only FS | `read_only: true` in compose |
| No new privileges | `security_opt: no-new-privileges:true` |
| Security headers | CSP, X-Frame-Options, XSS-Protection |
| Rate limiting | 30 req/s on API proxy |
| Server tokens | `server_tokens off` (hide nginx version) |

### 4.3 Network Security

- Docker bridge network isolates services
- Only frontend port (80) exposed to host
- Internal service-to-service communication only
- PostgreSQL not exposed to host in production

---

## 5. Source Code Protection

### 5.1 Frontend (Angular)

| Technique | Effectiveness | Notes |
|-----------|--------------|-------|
| AOT compilation | HIGH | Template compilation, no Angular runtime |
| Minification | MEDIUM | Variable names compressed |
| No source maps | HIGH | `.map` files excluded from prod build |
| Tree shaking | MEDIUM | Unused code removed |
| CSP headers | MEDIUM | Limits script injection |

**What an attacker sees:**
```javascript
// Minified, AOT-compiled Angular
!function(){...}();
// No readable component names, no templates
```

### 5.2 Backend (Spring Boot JAR)

| Technique | Effectiveness | Notes |
|-----------|--------------|-------|
| JAR packaging | MEDIUM | Source not directly visible |
| ProGuard/R8 | HIGH | Class/method name obfuscation |
| String encryption | MEDIUM | Hardcoded strings encrypted |
| License in binary | HIGH | Public key embedded in JAR |

**What an attacker sees:**
```bash
# Inside the JAR
BOOT-INF/classes/com/foreman/
  a.class  # Obfuscated class names
  b.class
  c.class
  # With ProGuard, even class names are meaningless
```

### 5.3 What Cannot Be Protected

- **JAR decompilation:** Java bytecode can always be decompiled (JD Pro, JADX)
- **Runtime memory:** Once running, code is in accessible memory
- **API analysis:** Network traffic can be intercepted and analyzed
- **Container inspection:** `docker exec` provides shell access (mitigated by read-only FS)

---

## 6. Container Hardening

### 6.1 Docker Compose Security

```yaml
security_opt:
  - no-new-privileges:true  # Prevents privilege escalation
read_only: true              # Filesystem immutable
tmpfs:                       # Writable temp directories only
  - /tmp
  - /app/data
```

### 6.2 Secrets Management

| Secret | Storage Method |
|--------|---------------|
| DB Password | `.env` file (not in git) |
| JWT Secret | `.env` file (not in git) |
| RSA Private Key | NEVER in container |
| License File | Mounted volume |
| API Keys | Environment variables |

### 6.3 Image Security

- Base images pinned to specific versions
- No `latest` tags in production
- Vulnerability scanning recommended: `docker scout cves`
- Private registry recommended for distribution

---

## 7. Deployment Workflow

### 7.1 Build Process

```bash
# 1. Integrate license system into source
cd secured-distribution
bash scripts/integrate-license.sh

# 2. Generate licenses
python license-system/generate_license.py --customer "Client Corp" --type annual --days 365

# 3. Build Docker images
cd docker
cp .env.example .env
# Edit .env with production values
docker-compose -f docker-compose.prod.yml build

# 4. Deploy
docker-compose -f docker-compose.prod.yml up -d

# 5. Verify
curl http://localhost/api/auth/login
# Should return 503 if no valid license
```

### 7.2 License Distribution

1. Generate license on secure machine (owner's laptop)
2. Copy `.license` file to deployment server
3. Mount or copy into container volume
4. Restart backend container
5. Verify license in logs

### 7.3 Docker Hub Push

```bash
# Tag images
docker tag jene-secured-api:latest YOUR_USER/jene-secured-backend:1.0.0
docker tag jene-secured-web:latest YOUR_USER/jene-secured-frontend:1.0.0

# Push
docker login
docker push YOUR_USER/jene-secured-backend:1.0.0
docker push YOUR_USER/jene-secured-frontend:1.0.0
```

---

## 8. Risk Analysis

### 8.1 Protection Effectiveness

| Attack Vector | Difficulty to Bypass | Time Estimate | Skill Required |
|--------------|---------------------|---------------|----------------|
| Run without license | HIGH | N/A | Expert |
| Modify license file | HIGH | Hours | Expert |
| Forge new license | VERY HIGH | Days/Weeks | Cryptography expert |
| Decompile frontend JS | MEDIUM | Hours | Web developer |
| Decompile backend JAR | MEDIUM | Hours | Java developer |
| Extract from running container | MEDIUM | Hours | DevOps engineer |
| Bypass Docker security | LOW-MEDIUM | Minutes-Hours | Linux admin |

### 8.2 Cost-Benefit for Attackers

- **Time to bypass:** 2-80 hours depending on approach
- **Skill required:** Senior developer or security engineer
- **Risk of detection:** Medium (license validation logs attempts)
- **Business value:** Low for most attackers (case management is commodity)

### 8.3 Recommendations

1. **Use private Docker registry** (Docker Hub private, ECR, GCR)
2. **Monitor license validation logs** for bypass attempts
3. **Rotate keys annually** if possible
4. **Add online license check** for high-value deployments
5. **Include license terms** in contract (legal deterrent)

---

## 9. Limitations & Realistic Expectations

### What This System CAN Do:
- Prevent casual unauthorized use
- Detect license tampering
- Block expired/invalid licenses
- Make reverse engineering time-consuming
- Provide audit trail of license validation
- Protect against redistribution

### What This System CANNOT Do:
- Prevent all reverse engineering (Java bytecode is always decompilable)
- Stop a determined attacker with unlimited time
- Protect against memory scraping
- Prevent API reverse-engineering
- Guarantee 100% copy protection

### Honest Assessment:
> **No software protection is unbreakable.** The goal is to make unauthorized use
> **more expensive than buying a license.** RSA-2048 signing combined with Docker
> hardening provides **strong practical protection** suitable for commercial software.
> A motivated attacker with source access can eventually bypass any protection.
> The economic deterrent is the primary security mechanism.

---

*Document generated by JENE-DFCMS Security Architecture Review*
*Version: 1.0 | Date: 2026-06-29*
