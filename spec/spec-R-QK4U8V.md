# Technical Specification — R-QK4U8V

## Metadata

- **Release ID:** R-QK4U8V
- **Discovery mode:** Delegated
- **Specialization:** baseline SDLC
- **Runtime target:** N/A
- **UX mode:** N/A
- **API mode:** N/A
- **Spec confirmed date:** 2026-10-02

---

## 1. Goals

- Upgrade the application's Java runtime from **Java 8 to Java 11** (LTS) while remaining on **Spring Boot 2.7.18** (which fully supports Java 11).
- Preserve all existing behavior: no functional, UI, data-model, or API changes.
- Keep the project a single, locally-runnable Docker monolith (no host Java/Maven, no cloud).
- Modernize the build and runtime toolchain (Maven build image and runtime JRE base image) to Java 11.

## 2. Stakeholders

- **Colleague / developer** — operator of the fixture; wants it on a newer LTS runtime.
- **Training / workshop participants** — continue to use the fixture unchanged, now on Java 11.
- **TWTTY AI coding agent** — performs the migration end-to-end under this methodology.

## 3. Success metrics

- **Build:** `mvn package` on JDK 11 completes with `BUILD SUCCESS`.
- **Tests:** all existing acceptance tests pass (4 of 4) on JDK 11.
- **Container:** `docker compose up --build` builds on the Java 11 toolchain and the home page returns HTTP 200 within ≤ 60 seconds, with the usual seeded data (~20 customers, 30 products, 50 orders).
- **No behavior change:** no application source files are modified; only the declared Java version and base images change.

## 4. Constraints

- Technology baseline shifts **only** the Java version: Java 11, Spring Boot 2.7.18 (unchanged), Maven, Spring MVC + Thymeleaf, Spring Data JPA, embedded H2 (in-memory). No Spring Boot 3 / `jakarta.*` migration in this release.
- The system MUST remain a single monolithic deployable; execution is local-only via Docker.
- No application-code changes: Java 8 source is source-compatible with Java 11; the upgrade is limited to `pom.xml` and the `Dockerfile` base images.
- Risk level is 1 (floor): reduced rigor; the fixture is not intended for real users, real or customer data, production traffic, or long-lived operation.
- The runtime container MUST continue to use pinned base-image versions, run as a non-root user, and publish only the application port.

## 5. Domains and use cases

### 5.1 Domains

Single domain — the whole system. This release changes the runtime platform only; the system context is unchanged from [R-Z7UL7V](spec-R-Z7UL7V.md).

## 6. Functional requirements

- **FR-1:** The application's declared Java version is 11, and it compiles and packages on a JDK 11 toolchain.
- **FR-2:** All pre-existing functional behavior (customers, products, inventory, orders, order lifecycle, seeded data) is preserved unchanged.

## 7. Non-functional requirements

- **NFR-1:** The container build and runtime stages use pinned Java 11 base images (`maven:3.9-eclipse-temurin-11`, `eclipse-temurin:11-jre`).
- **NFR-2:** The runtime container continues to run as a non-root user and publishes only port 8080.

## 8. Acceptance criteria

- **AC-1:** `pom.xml` declares `<java.version>11</java.version>` and `mvn package` on JDK 11 reports `BUILD SUCCESS`.
- **AC-2:** The existing acceptance test suite passes 4 of 4 on JDK 11.
- **AC-3:** `docker compose up --build` builds on the Java 11 images and serves HTTP 200 at `http://localhost:8080/` with seeded data present.
- **AC-4:** No files under `src/` are modified by this release (behavior-preserving upgrade).

## 14. Data classification

No data stored, processed, or transmitted by this release beyond the pre-existing fictional, in-memory demo data (unchanged from R-Z7UL7V). No PII; no real or customer data.
