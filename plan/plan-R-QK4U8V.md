# Plan — R-QK4U8V

## Metadata

- **Release ID:** R-QK4U8V
- **Plan confirmed date:** 2026-10-02

---

## 1. Architecture

### 1.1 Domains and application architecture

Single domain — the whole system. The architecture is **unchanged** from [R-Z7UL7V](plan-R-Z7UL7V.md); this release is a runtime-platform upgrade (Java 8 → 11) with no change to components C-1…C-6, their responsibilities, or their interactions. Spring Boot remains 2.7.18.

### 1.4 Security architecture

No change. The runtime container continues to run as a non-root user and publishes only port 8080; base images move to pinned Java 11 tags. No secrets, no network boundary crossed beyond the local published port (encryption-in-transit N/A for local-only per sdlc §7.1.6).

## 2. Design

### 2.1 Application design

Behavior-preserving upgrade. No application source changes. The only edits are the declared Java version and the container base images:

- `pom.xml`: `<java.version>8</java.version>` → `<java.version>11</java.version>`.
- `Dockerfile` build stage: `maven:3.9-eclipse-temurin-8` → `maven:3.9-eclipse-temurin-11`.
- `Dockerfile` runtime stage: `eclipse-temurin:8-jre` → `eclipse-temurin:11-jre`.

Rationale: Java 8 source is source- and binary-compatible with Java 11, and Spring Boot 2.7.18 supports Java 11 fully, so no code migration (and no `jakarta.*` namespace change) is required.

## 3. Orchestration

### 3.1 Work items

| W-<n> | Description | Expected file footprint |
|-------|-------------|-------------------------|
| **W-1** | Upgrade the Java runtime from 8 to 11: bump `pom.xml` `java.version` and the Dockerfile build + runtime base images; rebuild and re-validate. | `pom.xml`, `Dockerfile` |

### 3.2 Dependencies

Single work item; no dependencies.

### 3.3 Execution pattern

**Sequential** (one work item). Parallel is not applicable.

## Validation plan

1. Build + test on JDK 11 in the Maven container: `mvn package` → `BUILD SUCCESS`, 4/4 acceptance tests (AC-1, AC-2).
2. Container smoke: `docker compose up --build` on the Java 11 images, then `curl http://localhost:8080/` → HTTP 200 with seeded data (AC-3).
3. Confirm no `src/` files changed by this release (AC-4).
