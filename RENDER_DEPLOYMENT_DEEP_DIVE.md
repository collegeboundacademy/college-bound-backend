# Render Deployment Deep Dive (college-bound-backend)

This document captures the exact deployment model for the backend service on Render, including runtime setup, environment variables, and troubleshooting decisions made during production rollout.

## 1) Architecture Summary

- Platform: Render Web Service (Docker-based deploy)
- App: Spring Boot backend
- Runtime: Java 17 inside Docker image
- Public URL: https://college-bound-backend.onrender.com
- Primary public API used by frontend: `/api/colleges/*`

## 2) Why Docker Deploy Was Chosen

The initial native Render build path failed with Java runtime detection/JAVA_HOME issues. We moved to an explicit Dockerfile so runtime selection is deterministic.

Current Dockerfile strategy:

- Build stage image: `eclipse-temurin:17-jdk`
- Runtime stage image: `eclipse-temurin:17-jre`
- Build command inside container: `./mvnw clean package -DskipTests`
- Run command inside container: `java -jar app.jar`

This avoids Render auto-runtime ambiguity and pins Java to 17.

## 3) Required Render Environment Variables

Minimum required for College Explorer API functionality:

- `COLLEGE_SCORECARD_API_KEY`
- `COLLEGE_DATA_SOURCE=api`
- `FRONTEND_URL=https://collegeboundacademy.github.io/college-bound/`
- `CORS_ALLOWED_ORIGINS=https://collegeboundacademy.github.io`

Optional (only needed for auth login flows):

- `GITHUB_CLIENT_ID`
- `GITHUB_CLIENT_SECRET`

Optional (only needed if persistence is attached):

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## 4) Startup Behavior and Safety Defaults

Current backend defaults are designed to avoid hard failures when optional services are not configured yet:

- GitHub OAuth is conditionally enabled only if both `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` are non-empty.
- Missing OAuth credentials no longer crash startup.
- Datasource defaults still point to local MySQL-style values, but schema auto-migration is disabled:
  - `spring.jpa.hibernate.ddl-auto=none`
  - `spring.jpa.defer-datasource-initialization=true`

Important: If a datasource is reachable and expected, provide explicit datasource env vars for production.

## 5) CORS and Frontend Integration

Frontend host in production:

- `https://collegeboundacademy.github.io/college-bound/`

Backend CORS origin should be origin-only (no path):

- `https://collegeboundacademy.github.io`

Why this matters:

- Browser CORS compares origins only (`scheme + host + port`), not URL paths.
- Using the full path in allowed origins can fail origin matching.

## 6) API Validation Checklist After Deploy

Run these checks after each redeploy:

1. Health/API smoke test:
   - `GET https://college-bound-backend.onrender.com/api/colleges/search?limit=1`
2. Confirm response is JSON and includes `results` array.
3. Verify no CORS errors in browser when frontend calls backend.
4. Verify source metadata appears in search payload (`source`, `dataLastRefreshed` when available).

## 7) Known Failure Modes and Fixes

### A) `JAVA_HOME environment variable is not defined correctly`

Cause:

- Render native build path/runtime mismatch.

Fix used:

- Moved to Docker deploy using Java 17 images.

### B) Docker image tag not found

Cause:

- Nonexistent tag variants (`openjdk:17-slim`, `eclipse-temurin:17-jre-slim`) in mirror.

Fix used:

- Switched to stable tags:
  - `eclipse-temurin:17-jdk`
  - `eclipse-temurin:17-jre`

### C) `Client id of registration 'github' must not be empty`

Cause:

- OAuth client properties were defined but blank, forcing validation at startup.

Fix used:

- Removed hard binding to empty oauth properties.
- Added conditional OAuth configuration gate in `SecurityConfig`.

### D) MySQL communications link failure on Render

Cause:

- No reachable DB configured from Render environment.

Current mitigation:

- College Explorer endpoints are still deployable/usable with API-focused flow.
- Add DB env vars when auth persistence features are needed.

## 8) Security Notes

- Secrets are environment-driven, not hardcoded in source.
- If any credential was previously exposed in logs/history, rotate it.
- For OAuth callback correctness, ensure GitHub OAuth app callback URL matches deployed backend route.

## 9) Operational Runbook (Quick)

1. Push backend commits.
2. Render auto-builds from Dockerfile.
3. Confirm env vars exist in Render service.
4. Redeploy.
5. Smoke test `/api/colleges/search?limit=1`.
6. Validate frontend search and detail flows.

## 10) Source of Truth Files

- `Dockerfile`
- `src/main/resources/application.properties`
- `src/main/java/com/collegebound/demo/security/SecurityConfig.java`
- `src/main/java/com/collegebound/demo/college/CollegeExplorerController.java`
- `src/main/java/com/collegebound/demo/college/CollegeScorecardClient.java`
