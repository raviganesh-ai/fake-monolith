## What I Want To Build

A realistic but fictional legacy monolithic web application that can be used for demonstrations, training, architecture analysis, and modernization exercises.

Build it as a three-tier Java 8 application using Spring Boot 2.7.x and Maven:

- A traditional Spring MVC, server-rendered presentation tier for employees to use in a browser
- A Spring service-layer business tier containing the application's workflows and rules
- A Spring Data JPA relational data-access tier backed by a local database

The application should model a small order-management system where employees can manage customers, products, inventory, and orders. All tiers should live in one codebase, run in one process, and be packaged as one executable Spring Boot application. Use conventions typical of an older enterprise Spring application, including synchronous request handling, server-rendered pages, package-by-layer organization, shared domain entities, and tightly coupled in-process modules. Do not split the system into microservices, separate front-end applications, or independently deployable components.

Local execution is the primary runtime target, using containers so the legacy Java toolchain does not need to be installed on the host. The application should use an embedded H2 database by default, initialize its schema and fictional sample data automatically, and require no cloud account, external database, or third-party service. Provide a multi-stage container build that compiles the application with Java 8 and Maven and runs the packaged application in a Java 8 runtime image. Provide Docker Compose configuration so a developer with only Docker installed can build and start the complete application with one documented command, open it at a documented localhost URL, and stop and remove its containers with one documented teardown command.

The runtime container should use pinned base-image versions, run as a non-root user, expose only the application port, and avoid unnecessary host filesystem mounts or privileges.

Use only fictional sample data. The legacy character should come from the architecture and older framework generation, not from intentionally exploitable security flaws. Do not include real credentials, personal information, deliberately vulnerable dependencies, or integrations with production services.

## Done Looks Like

- A Java web application provides browser pages for managing customers, products, inventory, and orders
- Users can create and view customers, create and update products, adjust inventory, place orders, and review order history
- The presentation, business-logic, and data-access tiers are visibly separated in packages but shipped as one monolithic deployable
- The application builds with Java 8 and Maven and runs on Spring Boot 2.7.x
- The web interface uses Spring MVC with server-rendered HTML rather than a separate JavaScript front end
- Persistence uses Spring Data JPA and a relational database
- The application uses an embedded H2 relational database by default and initializes it automatically with fictional demonstration data
- Business rules include inventory validation, order-total calculation, and order-status transitions
- The repository includes automated tests for the main business workflows
- The repository includes clear instructions for building, testing, and running the application locally
- A developer with Docker installed can build and start the complete application with one documented Docker Compose command
- Java 8 and Maven run inside the container build and do not need to be installed on the host
- The running application is available through a documented `http://localhost:<port>` URL
- Local startup requires no cloud credentials, external services, or external database
- A documented Docker Compose teardown command stops and removes the local application containers
- The runtime container uses pinned base images, a non-root user, minimal privileges, and no unnecessary host filesystem mounts
- The codebase is substantial enough to support later refactoring or modernization exercises, without deliberately introducing dangerous vulnerabilities
