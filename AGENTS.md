# Repository Guidelines

## Project Structure & Module Organization
This repository is a Java 21 multi-module Maven project. The root [`pom.xml`](/home/hespway/Workspace/Projects/Java/banking-kata-api/pom.xml) aggregates three modules:

- `banking-app`: Spring Boot entrypoint and runtime config in `src/main/resources/`.
- `banking-account`: account, user, controller, service, repository, and DTO logic.
- `banking-shared`: shared security, exception, interceptor, and response infrastructure.

Production code lives under `src/main/java`. Tests currently live under `banking-account/test/java`, so keep new tests consistent with the existing layout unless the test structure is refactored across the module.

## Build, Test, and Development Commands
- `mvn clean test`: runs the full Maven test suite from the repository root.
- `mvn clean package`: builds all modules and produces the Spring Boot artifact.
- `mvn -pl banking-app spring-boot:run`: starts the API locally.
- `docker compose up -d`: starts the local PostgreSQL dependency defined in [`docker-compose.yml`](/home/hespway/Workspace/Projects/Java/banking-kata-api/docker-compose.yml).

Local runtime uses PostgreSQL on `localhost:5432/bankdb`; tests use the H2-backed `test` profile from [`application-test.yml`](/home/hespway/Workspace/Projects/Java/banking-kata-api/banking-app/src/main/resources/application-test.yml).

## Coding Style & Naming Conventions
Use 4-space indentation and standard Java naming: `PascalCase` for classes, `camelCase` for methods/fields, and lowercase package names under `com.bank...`. Keep controllers, services, repositories, DTOs, and entities in their existing dedicated packages. Prefer clear, single-purpose service methods and align DTO names with endpoint behavior, for example `LoginRequest` or `StatementDto`.

This project uses Lombok and MapStruct. No formatter or linter is configured in Maven, so keep imports clean and follow existing Spring Boot conventions.

## Testing Guidelines
Tests use JUnit 5, Spring Boot Test, MockMvc, AssertJ, and the `test` Spring profile. Name test classes with the `*Test` suffix and use descriptive method names such as `shouldCreateAdditionalAccountForUser`. Favor API-level tests that extend the existing `BaseApiTest` when validating controller behavior, authentication, and transactional flows.

## Commit & Pull Request Guidelines
Recent history uses short imperative subjects such as `Add docker config && auth api` and `Initialize account interfaces definitions`. Keep commits focused, use present-tense verbs (`Add`, `Refactor`, `Fix`), and avoid leaving `WIP` commits in shared branches.

Pull requests should explain the functional change, note any config or schema impact, and list the verification performed (`mvn clean test`, manual API checks, or Docker startup). Include example requests or responses when API behavior changes.
