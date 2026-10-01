# Plan — R-Z7UL7V

## Metadata

- **Release ID:** R-Z7UL7V
- **Plan confirmed date:** 2026-10-01

---

## 1. Architecture

### 1.1 Domains and application architecture

Single domain — the whole system.

**Components** (legacy package-by-layer; shared entities across layers):

- **C-1:** Web layer — Spring MVC controllers and Thymeleaf views; request handling, form binding, and validation.
- **C-2:** Service layer — business logic for order placement, inventory validation, order-total calculation, status transitions, and cancel-restock.
- **C-3:** Repository layer — Spring Data JPA repositories for Customer, Product, and Order.
- **C-4:** Domain model — shared JPA entities (Customer, Product, Order, OrderItem) used across all layers.
- **C-5:** Data initializer — seeds the schema and fictional sample data at startup.
- **C-6:** Application bootstrap and configuration — Spring Boot main class and configuration (H2, Thymeleaf, server port).

```mermaid
flowchart TD
    browser["Operations employee browser"]:::actor
    subgraph app["Order Management Monolith (one Spring Boot process)"]
        web["C-1 Web layer (MVC and Thymeleaf)"]:::compute
        svc["C-2 Service layer (business rules)"]:::compute
        repo["C-3 Repository layer (Spring Data JPA)"]:::compute
        dom["C-4 Domain model (shared entities)"]:::compute
        init["C-5 Data initializer"]:::compute
        boot["C-6 Bootstrap and config"]:::compute
        h2[("Embedded H2 in memory")]:::data
    end
    browser -->|HTTP| web --> svc --> repo --> h2
    init --> h2
    boot --> web
    web -.uses.-> dom
    svc -.uses.-> dom
    repo -.uses.-> dom
    classDef actor fill:#E3F2FD,stroke:#1565C0,color:#111;
    classDef compute fill:#E8F5E9,stroke:#2E7D32,color:#111;
    classDef data fill:#F3E5F5,stroke:#6A1B9A,color:#111;
```

### 1.2 Data architecture

Single in-memory H2 database, created and seeded at startup, discarded on container stop (ephemeral, internal fictional data). Entities and relationships:

```mermaid
erDiagram
    CUSTOMER ||--o{ ORDER : places
    ORDER ||--|{ ORDER_ITEM : contains
    PRODUCT ||--o{ ORDER_ITEM : "ordered in"
    CUSTOMER {
        Long id
        String name
        String email
        String phone
        String address
    }
    PRODUCT {
        Long id
        String sku
        String name
        BigDecimal unitPrice
        int quantityOnHand
    }
    ORDER {
        Long id
        Long customerId
        LocalDateTime orderDate
        String status
        BigDecimal totalAmount
    }
    ORDER_ITEM {
        Long id
        Long orderId
        Long productId
        int quantity
        BigDecimal unitPriceAtOrder
        BigDecimal lineTotal
    }
```

### 1.3 Infrastructure architecture

Local only. A single Docker Compose service runs the Spring Boot application in one container publishing port 8080 to the host; the H2 database is embedded in-process (no separate database container). No cloud, no external services. Deployment strategy and rollback are not declared (risk level below 3).

```mermaid
flowchart LR
    user["Operations employee browser"]:::actor
    subgraph laptop["Developer laptop (Docker Engine)"]
        subgraph compose["docker compose"]
            appc["App container (Spring Boot, port 8080)"]:::compute
            h2[("Embedded H2 in memory")]:::data
            appc --- h2
        end
    end
    user -->|"http localhost 8080"| appc
    classDef actor fill:#E3F2FD,stroke:#1565C0,color:#111;
    classDef boundary fill:#FFF8E1,stroke:#F9A825,color:#111;
    classDef compute fill:#E8F5E9,stroke:#2E7D32,color:#111;
    classDef data fill:#F3E5F5,stroke:#6A1B9A,color:#111;
    class laptop,compose boundary;
```

### 1.4 Security architecture

Required because spec §14.1 declares `internal` data. Controls:

- **Encryption in transit:** all traffic is loopback between the browser and the container on a single developer host; no data crosses a network boundary, so TLS is not applied. If the app were ever exposed beyond localhost, TLS would be required.
- **Encryption at rest:** not applicable — data is ephemeral in-memory only and no `confidential`/`restricted` class is present.
- **Authentication/authorization:** none, by spec decision (single implicit operator).
- **Secrets:** none stored; secret scanning applies at 3d.

```mermaid
flowchart LR
    browser["Browser"]:::actor
    subgraph host["Single host trust boundary (loopback only)"]
        appc["App container (HTTP, no TLS)"]:::compute
        h2[("H2 in memory, ephemeral")]:::data
        appc --- h2
    end
    browser -->|"loopback HTTP, no network boundary crossed"| appc
    classDef actor fill:#E3F2FD,stroke:#1565C0,color:#111;
    classDef boundary fill:#FFF8E1,stroke:#F9A825,color:#111;
    classDef compute fill:#E8F5E9,stroke:#2E7D32,color:#111;
    classDef data fill:#F3E5F5,stroke:#6A1B9A,color:#111;
    class host boundary;
```

### 1.5 Technology choices

- **Currency:** Options were looked up at plan time (2026-10-01). Spring Boot 2.7.x reached end of OSS support in June 2023 and is the last line supporting Java 8; the current line is Spring Boot 4.1.x, which requires Java 17 or newer. Sources consulted: endoflife.date/spring-boot and versionlog.com/spring-boot (treated as untrusted data). The intentionally end-of-life stack is deliberate — it is what makes this a realistic legacy/modernization fixture.
- **Human User preferences:** Confirmed the intentionally-legacy Java 8 + Spring Boot 2.7.x stack; accepted the JUnit 5 and plain-CSS defaults; no other preferences stated.

- Java 8 — the legacy runtime the fixture targets.
- Spring Boot 2.7.x — last line supporting Java 8; the intentionally end-of-life framework generation.
- Maven — traditional enterprise build tool.
- Spring MVC with Thymeleaf — server-rendered HTML views.
- Spring Data JPA with Hibernate — ORM data-access layer.
- H2 (in-memory) — embedded, auto-seeded database requiring no external service.
- JUnit 5 with Spring Boot Test and MockMvc — Spring Boot 2.7 default test stack.
- Plain CSS (no build step) — minimal legacy-styled UI without a front-end toolchain.
- `maven:3.8-eclipse-temurin-8` (build stage) and `eclipse-temurin:8-jre` (runtime stage), version-pinned — multi-stage container keeps Java 8 and Maven off the host.

```mermaid
flowchart TD
    subgraph client["Client"]
        br["Browser (server-rendered HTML)"]:::actor
    end
    subgraph runtime["Spring Boot 2.7.x on Java 8"]
        mvc["Spring MVC and Thymeleaf"]:::compute
        service["Service layer"]:::compute
        jpa["Spring Data JPA and Hibernate"]:::compute
        db[("H2 in memory")]:::data
    end
    subgraph build["Build and packaging"]
        mvn["Maven"]:::cicd
        docker["Docker multi-stage and Compose"]:::cicd
    end
    br --> mvc --> service --> jpa --> db
    mvn --> docker
    classDef actor fill:#E3F2FD,stroke:#1565C0,color:#111;
    classDef compute fill:#E8F5E9,stroke:#2E7D32,color:#111;
    classDef data fill:#F3E5F5,stroke:#6A1B9A,color:#111;
    classDef cicd fill:#ECEFF1,stroke:#455A64,color:#111;
```

### 1.6 Test architecture

Minimal suite covering only the four tested acceptance criteria (AC-1 through AC-4). Integration tests use `@SpringBootTest` with MockMvc; unit tests cover the service-layer business rules. No end-to-end, performance, or accessibility tests. No numeric coverage floor applies (diff and cumulative floors begin at risk level 3). Tests live under `src/test/java`.

```mermaid
graph TD
    e2e["End to end none (out of scope)"]:::top
    integ["Integration MockMvc tests for AC-1 to AC-4"]:::mid
    unit["Unit service tests for totals, stock validation, cancel restock"]:::base
    e2e --> integ --> unit
    classDef top fill:#FFEBEE,stroke:#C62828,color:#111;
    classDef mid fill:#FFF8E1,stroke:#F9A825,color:#111;
    classDef base fill:#E8F5E9,stroke:#2E7D32,color:#111;
```

### 1.7 Operations architecture

None for this release.

## 2. Design

### 2.1 Application design

- **D-1:** Controllers — `CustomerController`, `ProductController`, `OrderController`. Thin controllers that bind form submissions, run `BindingResult` validation, call the service layer, and select Thymeleaf views.
  - **Refines:** C-1
- **D-2:** Service interfaces — `CustomerService`, `ProductService`, `OrderService` with `@Transactional` business methods (create/update entities, adjust stock, place order, change order status with restock on cancel).
  - **Refines:** C-2
- **D-3:** Repositories — interfaces extending `JpaRepository<Entity, Long>` for Customer, Product, and Order.
  - **Refines:** C-3
- **D-4:** Entities — `Customer`, `Product`, `Order`, `OrderItem` with JPA mappings and an `OrderStatus` enum (NEW, CONFIRMED, SHIPPED, CANCELLED). `OrderItem` captures `unitPriceAtOrder` and `lineTotal`.
  - **Refines:** C-4
- **D-5:** `DataInitializer` — an `ApplicationRunner` that seeds approximately 20 customers, 30 products, and 50 orders at startup.
  - **Refines:** C-5
- **D-6:** Configuration — `application.properties` configuring in-memory H2, `ddl-auto=create`, Thymeleaf, and server port 8080; Spring Boot main class.
  - **Refines:** C-6

**UC-1 Manage customers (create)**

```mermaid
sequenceDiagram
    actor Emp as Operations employee
    participant CC as CustomerController
    participant CS as CustomerService
    participant CR as CustomerRepository
    Emp->>CC: Submit new customer form (name and optional fields)
    CC->>CC: validate name present (BindingResult)
    CC->>CS: createCustomer(data)
    CS->>CR: save(customer)
    CS-->>CC: saved customer
    CC-->>Emp: redirect to customer list
```

**UC-2 Manage products (create or edit)**

```mermaid
sequenceDiagram
    actor Emp as Operations employee
    participant PC as ProductController
    participant PS as ProductService
    participant PR as ProductRepository
    Emp->>PC: Submit product form (sku, name, unitPrice)
    PC->>PC: validate price non-negative (BindingResult)
    PC->>PS: saveProduct(data)
    PS->>PR: check SKU unique
    PS->>PR: save(product)
    PS-->>PC: saved product
    PC-->>Emp: redirect to product list
```

**UC-3 Manage inventory (adjust stock)**

```mermaid
sequenceDiagram
    actor Emp as Operations employee
    participant PC as ProductController
    participant PS as ProductService
    participant PR as ProductRepository
    Emp->>PC: Submit adjust stock form (productId, newQuantity)
    PC->>PC: validate quantity non-negative
    PC->>PS: adjustStock(productId, newQuantity)
    PS->>PR: load product
    PS->>PR: save(product with quantityOnHand)
    PS-->>PC: updated product
    PC-->>Emp: redirect to product stock list
```

**UC-4 Place order**

```mermaid
sequenceDiagram
    actor Emp as Operations employee
    participant OC as OrderController
    participant OS as OrderService
    participant PR as ProductRepository
    participant OR as OrderRepository
    Emp->>OC: Submit new order (customer, line items)
    OC->>OS: placeOrder(customerId, lines)
    OS->>PR: load products and stock
    OS->>OS: combine duplicate lines, validate qty vs stock, compute totals
    OS->>PR: decrement stock on hand
    OS->>OR: save order (status NEW)
    OS-->>OC: order id
    OC-->>Emp: redirect to order detail
```

**UC-5 Review orders (advance or cancel)**

```mermaid
sequenceDiagram
    actor Emp as Operations employee
    participant OC as OrderController
    participant OS as OrderService
    participant OR as OrderRepository
    participant PR as ProductRepository
    Emp->>OC: Advance or cancel order (orderId, action)
    OC->>OS: changeStatus(orderId, action)
    OS->>OR: load order
    OS->>OS: validate transition legal
    alt cancel
        OS->>PR: restock item quantities
    end
    OS->>OR: save order (new status)
    OS-->>OC: updated order
    OC-->>Emp: redirect to order detail
```

### 2.2 Data design

None for this release.

### 2.3 Infrastructure design

None for this release.

### 2.4 Security design

None for this release.

### 2.5 Test design

None for this release.

### 2.6 UX design

**Design foundations.** No heavy design system; a plain-CSS, utilitarian legacy layout built from tables and forms, a system font, a limited palette, and standard spacing. This keeps the look period-accurate for an old enterprise app and avoids a front-end toolchain.

**Information architecture.** A persistent top navigation with Customers, Products, Inventory, and Orders. Each section presents a list view (table) and a form view; every screen has one clear primary action (for example, New customer). Empty states render a short "No records" message; errors render a generic error page, and form errors render inline next to the fields.

**Content design.** Terse, plain labels; error messages state what went wrong and what to do next.

```mermaid
flowchart LR
    nav["Top navigation"]:::actor
    subgraph cust["Customers"]
        cl["Customer list"] --> cf["Customer form"]
    end
    subgraph prod["Products"]
        pl["Product list"] --> pf["Product form"]
    end
    subgraph inv["Inventory"]
        il["Product stock list"] --> ia["Adjust stock form"]
    end
    subgraph ord["Orders"]
        no["New order form"] --> ol["Order list"]
        ol --> od["Order detail"]
        od --> os["Advance or cancel status"]
    end
    nav --> cl
    nav --> pl
    nav --> il
    nav --> ol
    nav --> no
    classDef actor fill:#E3F2FD,stroke:#1565C0,color:#111;
```

```mermaid
stateDiagram-v2
    [*] --> NEW
    NEW --> CONFIRMED
    CONFIRMED --> SHIPPED
    NEW --> CANCELLED
    CONFIRMED --> CANCELLED
    SHIPPED --> [*]
    CANCELLED --> [*]
```

### 2.7 API design

N/A per spec §15 (release ships no external API).

## 3. Orchestration

### 3.1 Work breakdown

Single domain — every work item belongs to the whole-system domain, and each footprint is within the single repository.

| ID | Domain | Title | Description | Expected file footprint | Traces to |
|----|--------|-------|-------------|-------------------------|-----------|
| W-1 | whole system | Scaffolding and configuration | Create the Maven `pom.xml` (Java 8, Spring Boot 2.7.x, dependencies: web, thymeleaf, data-jpa, h2, test), the Spring Boot main class, and `application.properties` (in-memory H2, `ddl-auto=create`, Thymeleaf, port 8080). | `pom.xml`, `src/main/resources/**`, `src/main/java/**/Application.java`, `src/main/java/**/config/**` | C-6, FR-1 |
| W-2 | whole system | Domain model | Implement the shared JPA entities Customer, Product, Order, and OrderItem, plus the OrderStatus enum, with mappings and relationships. | `src/main/java/**/domain/**` | C-4, FR-2 |
| W-3 | whole system | Repository layer | Implement Spring Data JPA repository interfaces for Customer, Product, and Order. | `src/main/java/**/repository/**` | C-3, FR-2 |
| W-4 | whole system | Service layer | Implement CustomerService, ProductService, and OrderService with the business rules: SKU uniqueness, stock adjustment, transactional order placement (combine duplicate lines, validate stock, compute totals, decrement stock), status transitions, and cancel-restock. | `src/main/java/**/service/**` | C-2, FR-2, FR-3, FR-4, FR-5, FR-6 |
| W-5 | whole system | Web layer | Implement controllers, Thymeleaf templates, and plain CSS for the home page, customers, products, inventory, and orders, plus a generic error page and inline validation. | `src/main/java/**/web/**`, `src/main/resources/templates/**`, `src/main/resources/static/**` | C-1, FR-1 |
| W-6 | whole system | Data initializer | Implement an ApplicationRunner that seeds approximately 20 customers, 30 products, and 50 orders of fictional data at startup. | `src/main/java/**/init/**` | C-5, FR-1 |
| W-7 | whole system | Tests for AC-1 to AC-4 | Implement the minimal integration and service tests covering AC-1 (app starts, home returns 200), AC-2 (valid order decrements stock and computes totals), AC-3 (over-stock order rejected transactionally), and AC-4 (cancel restocks). Each test references its AC-ID. | `src/test/**` | C-2, FR-1, FR-2, FR-3, FR-4, FR-5, FR-6 |
| W-8 | whole system | Containerization | Create the multi-stage Dockerfile (Maven and Java 8 build stage, Java 8 JRE runtime stage, non-root user, pinned base images), `docker-compose.yml` publishing port 8080, `.dockerignore`, and the README build/run/teardown instructions. | `Dockerfile`, `docker-compose.yml`, `.dockerignore`, `README.md` | C-6, FR-1 |

### 3.2 Sequencing and dependencies

- **W-2 depends on:** W-1
- **W-3 depends on:** W-2
- **W-4 depends on:** W-2, W-3
- **W-5 depends on:** W-4
- **W-6 depends on:** W-2, W-3
- **W-7 depends on:** W-4, W-5, W-6
- **W-8 depends on:** W-1

```mermaid
graph TD
    W1["W-1 scaffolding"] --> W2["W-2 domain"]
    W2 --> W3["W-3 repositories"]
    W2 --> W4["W-4 services"]
    W3 --> W4
    W4 --> W5["W-5 web"]
    W2 --> W6["W-6 data init"]
    W3 --> W6
    W4 --> W7["W-7 tests"]
    W5 --> W7
    W6 --> W7
    W1 --> W8["W-8 container"]
```
