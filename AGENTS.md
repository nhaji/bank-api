# Repository Guidelines

## Project Structure & Module Organization
This is a Java 21 multi-module Maven project. The root `pom.xml` aggregates:
- `banking-shared`: cross-cutting Spring infrastructure only (`security`, `exceptions`, `interceptors`, shared DTOs).
- `banking-account`: account feature module using `api`, `application`, `domain`, `infrastructure`.
- `banking-user`: feature-first module with `auth`, `management`, and `shared` packages.
- `banking-app`: thin assembly module with the Spring Boot entrypoint and full integration tests.

Keep module boundaries explicit. Feature modules may depend on a provider-owned facade from another module, but never on another module’s repositories or entities directly.

## Build, Test, and Development Commands
- `mvn clean test`: runs the full Maven test suite from the repository root.
- `mvn clean package`: builds all modules and produces the Boot artifact.
- `mvn -pl banking-app spring-boot:run`: starts the API locally.
- `mvn -pl banking-app -am test`: runs the app module with required reactor dependencies.
- `docker compose up -d`: starts the local PostgreSQL dependency defined in [`docker-compose.yml`](/home/hespway/Workspace/Projects/Java/banking-kata-api/docker-compose.yml).

Local runtime uses PostgreSQL. Tests use H2 with the `test` profile in each module.

## Coding Style & Naming Conventions
Use 4-space indentation and standard Java naming: `PascalCase` for types, `camelCase` for members, lowercase packages under `com.bank...`.

Prefer feature-first packaging. Example:
- `com.bank.user.auth.api`
- `com.bank.user.management.application`
- `com.bank.user.shared.domain`

Controllers are concrete classes in `api`. Business logic lives in `application`. JPA entities belong in `domain` or feature-local/shared domain. Repositories, mappers, and security adapters belong in `infrastructure`.

All final API responses must be wrapped in `ResponseDTO`. Successful responses are wrapped by `ResponseWrapperAdvice`; errors must be produced through `GlobalExceptionHandler`, with `error` filled and `data` omitted.

## Testing Guidelines
Use JUnit 5, Spring Boot Test, MockMvc, AssertJ, and the `test` profile. Name test classes `*Test` and use descriptive methods such as `shouldRegisterNewUserAndProvisionInitialAccount`.

Testing strategy:
- Module API tests stay inside each feature module and should isolate cross-module calls with mocks or Spring Security test helpers.
- App-level integration tests live in `banking-app` and must use the assembled context with H2 and no mocks.
- Do not put all tests in `banking-app`; reserve it for real end-to-end verification.

## Commit & Pull Request Guidelines
Recent history uses short imperative subjects such as `Add docker config && auth api` and `Initialize account interfaces definitions`. Keep commits focused, use present-tense verbs (`Add`, `Refactor`, `Fix`), and avoid leaving `WIP` commits in shared branches.

Pull requests should explain the functional change, note config or schema impact, and list verification performed (`mvn clean test`, `mvn -pl banking-app -am test`, local startup). Include example requests/responses when API contracts change.
