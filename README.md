# Fake Legacy Monolith — Order Management

> A deliberately legacy-style, three-tier **Java 8 / Spring Boot 2.7** order-management monolith — server-rendered, one deployable, runs locally in Docker. Built as a fictional fixture for demos, training, and **modernization exercises**. Delivered end-to-end by an AI coding agent following the **Promptless Agentic SDLC (TWTTY)** methodology using the **baseline SDLC** specialized implementation.

![Methodology](https://img.shields.io/badge/methodology-Promptless%20Agentic%20SDLC-purple)
![Stack](https://img.shields.io/badge/stack-Java%208%20%7C%20Spring%20Boot%202.7-orange)
![Risk level](https://img.shields.io/badge/risk%20level-1%20(prototype)-lightgrey)

> **This is a throwaway demo/training fixture.** It uses only fictional data and is **not** intended for real users, real or customer data, production traffic, or long-lived operation as a real system. Its "legacy" character comes from the intentionally end-of-life framework generation and tightly coupled, package-by-layer architecture — **not** from deliberately introduced vulnerabilities.

---

## What it does

A single Spring Boot process lets an internal **operations employee** manage **customers, products, inventory, and orders** through server-rendered browser pages. Core business rules are enforced in a service layer: stock validation on order placement, order-total calculation, order-status transitions, and cancel-restock.

All three tiers — Spring MVC presentation, service-layer business logic, and Spring Data JPA data access — live in **one codebase** and ship as **one deployable**, backed by an embedded in-memory H2 database that re-seeds fictional sample data on every startup.

## Try it locally (Docker only)

> Java 8 and Maven run **inside** the container build — nothing legacy is installed on your host. Docker is the only prerequisite.

```powershell
# from the repository root
docker compose up --build
# then open http://localhost:8080
docker compose down
```

*(Build/run wiring is delivered by work item W-8 during EXECUTE; see the plan.)*

## How this was built

This fixture was delivered end-to-end by an AI coding agent following the **Promptless Agentic SDLC (TWTTY)** methodology, baseline **SDLC** implementation. The intent was captured in plain English in [`seed/seed.md`](seed/seed.md); the agent then drove specification, planning, and implementation, checking with the human at each stage-exit gate.

Every decision is recorded in an append-only replay-execution log that any future contributor (or AI agent) can read to understand or reproduce the release:

- [`seed/seed.md`](seed/seed.md) — human intent (SEED)
- [`spec/spec-R-Z7UL7V.md`](spec/spec-R-Z7UL7V.md) — requirements, use cases, acceptance criteria (SPEC)
- [`plan/plan-R-Z7UL7V.md`](plan/plan-R-Z7UL7V.md) — architecture, design, W-1…W-8 work breakdown (PLAN)
- [`replay-execution/R-Z7UL7V-execution-log.md`](replay-execution/R-Z7UL7V-execution-log.md) — append-only log of every approved step
- [`replay-execution/R-Z7UL7V-execution-diagram.md`](replay-execution/R-Z7UL7V-execution-diagram.md) — visual summary of the stages and gates

---

<!-- The deeper sections (architecture, showcase, controls, clone-and-run detail, continue-with-TWTTY)
     are filled in incrementally through EXECUTE and completed at 3m Iteration before EXECUTE-EXIT. -->
