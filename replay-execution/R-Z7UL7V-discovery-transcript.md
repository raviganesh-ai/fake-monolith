# Discovery Transcript — R-Z7UL7V

Project: `fake-monolith` · Specialization: baseline `sdlc` · Risk level: 1 (floor)
Discovery mode: **Delegated** · UX mode: **Delegated** · API mode: **N/A**

> Delegated-mode transcript (core §1): the AI Agent's proposed round-by-round chunks, the Human User's approvals/refinements, and the AI's rationale for every non-trivial choice. This is a first-class audit artifact; individual rounds are not separately logged in the replay-execution log.

## Up-front mode questions

- **Discovery mode:** AI recommended Delegated → Human User: "I accept your recommendations" → **Delegated**.
- **UX mode:** AI recommended Delegated (app ships a server-rendered employee UI) → **Delegated**.
- **API mode:** AI recommended N/A (server-rendered HTML only; no external network API) → **N/A**.
- **Mismatch check:** seed describes a server-rendered Spring Boot CRUD web app — matches baseline `sdlc` (not agentic, not dataeng). No mismatch.

---

## Round 1 — Surface

**AI proposal (summary):**
- Understanding of seed: fictional legacy three-tier Spring Boot 2.7 / Java 8 monolith; order-management; server-rendered (Spring MVC); Spring Data JPA → embedded H2; one deployable; local Docker run; Level 1 throwaway fixture.
- Persona: single **Operations employee** (only persona).
- Primary use cases: (1) customer management, (2) product management, (3) inventory management, (4) order placement, (5) order review.
- Success signals: builds/runs via `docker compose up`; reachable at localhost; all five workflows usable; business rules enforced; tests pass; tiers separated; codebase legacy-shaped for modernization.
- Out-of-scope: payments, shipping, reporting/analytics, multi-tenancy, notifications, real auth/SSO, cloud deploy.

**Disclosed non-trivial choice & rationale:**
- **Authentication = none (single implicit operator).** Rationale: seed never mentions login/roles; a local Level 1 fixture stores nothing sensitive, so no auth keeps it simple. Classic session-based login offered as a legacy alternative; AI defaulted to no auth.

**Human User response:** **Approved** (accepted the no-auth default).

---

## Round 2 — Deep-dive

**AI proposal (summary):**
- Data model: Customer, Product (with `quantityOnHand`), Order, OrderItem (with `unitPriceAtOrder`, `lineTotal`).
- Workflows: customer CRUD (create/list); product create/edit; inventory adjust; order placement with stock validation + total calculation; order review with status transitions.
- NFR defaults (measurable): page/action < 1000 ms locally with seeded data; ~20 customers / 30 products / 50 orders seeded; no hardcoded secrets; no real PII; single local instance (no SLA); default console logging; intentional legacy coupling.
- Data classification (§14): fictional data, Internal/Public, no PII, ephemeral (in-memory).

**Disclosed non-trivial choices & rationale:**
- **View tech = Thymeleaf.** Rationale: clean server-rendering inside an executable jar + container; JSP is more period-accurate but painful with Spring Boot's embedded container. AI defaulted to Thymeleaf.
- **Package-by-layer** (`web`/`service`/`repository`/`domain`/`config`) with shared entities. Rationale: the legacy coupling the seed explicitly wants, and a good modernization-exercise target.
- **DB = embedded H2, in-memory, re-seeded every startup.** Rationale: matches "initializes automatically"; simplest for a demo. File-persistent H2 offered as alternative.
- **Inventory = single `quantityOnHand` field on Product** (no warehouses/locations). Rationale: simplest realistic model.
- **Order state machine:** `NEW → CONFIRMED → SHIPPED`, `CANCELLED` from NEW/CONFIRMED, cancel restocks, illegal transitions rejected.

**Human User response:** **Approved** (accepted all disclosed defaults).

---

## Round 3 — Edge cases

**AI proposal (summary):**
- Validation/boundary rules: order qty positive and ≤ stock; empty order rejected; product price ≥ 0; unique SKU; stock ≥ 0; customer name required, email optional/loose.
- Order integrity: transactional all-or-nothing placement; `unitPriceAtOrder` frozen; status-machine rules; cancel restocks; shipped cannot be cancelled.
- Error handling: unknown ID → 404 page; form errors re-render with field messages; generic error page with no stack traces.
- Adversarial baseline: Thymeleaf auto-escaping (XSS), JPA parameterized queries (SQLi), field-length limits; no deliberately introduced vulnerabilities; H2 console disabled by default.
- Concurrency/abandonment: no concurrency control (single operator); no order draft state.
- Runtime: in-memory H2 resets on restart (documented); configurable port; Docker prerequisite/troubleshooting notes; teardown removes ephemeral data.

**Disclosed non-trivial choices & rationale:**
- **Duplicate product line in one order = combined (quantities summed).** Rationale: friendlier than rejecting; reject offered as alternative.
- **Transactional placement**, **no stack traces to users**, **H2 console off by default**, **no concurrency control** — all disclosed as the prototype-appropriate defaults.

**Human User response:** **Approved** (accepted the disclosed defaults).

---

## Round 4 — Acceptance criteria

**AI initial proposal:** a 26-AC matrix (AC-1…AC-26) covering build/container/run, functional behavior, architecture, security, performance, and UX/accessibility, each with assessment method, evidence, and pass condition; plus two open decisions (a11y-in-CI location, performance-measurement method).

**Human User refinement:** "This is a prototype, don't need any testing."

**AI response (constraint surfaced):** Explained that zero testing is not available under the methodology — 3f Test is in the Level 1 applied set, §7.1.2 requires an AC-linked test at every level, and EXECUTE-EXIT condition 2 requires every AC to trace to a passing test; dropping all of it would weaken core §0. Proposed the minimal compliant alternative: a ~4-test smoke/business-rule suite, dropping performance and automated-a11y ACs and leaving edge-case behaviors built-but-untested.

**Human User decision:** Chose option **1 — approve the minimal set.**

**Final acceptance criteria (the only tested criteria):**
- **AC-1** — App starts; `http://localhost:<port>` returns HTTP 200.
- **AC-2** — Placing a valid order decrements stock and computes line totals + order total correctly.
- **AC-3** — An over-stock order is rejected transactionally (no stock change, nothing persisted).
- **AC-4** — Cancelling an order restocks its items.

**Dropped (per Human User direction):** performance timing AC, automated accessibility test. UX §13 retains design intent only (semantic HTML, labelled inputs, keyboard-operable) — not tested. Edge-case/validation behaviors from Rounds 2–3 are still built but not elevated to separately-tested ACs.

**Residual issue to surface at SPEC-EXIT:** the UX quality bar (§10) expects automated accessibility testing in CI; at Level 1 there is no CI and the Human User declined additional testing, so a11y is a non-tested design target. This reduction is recorded for the Human User's informed SPEC-EXIT approval.

---

## Termination

AI proposed: "End of discovery; proceed to 1b Business requirements." Human User: **Approved**.
