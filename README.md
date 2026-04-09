# Bank API

Lightweight quickstart for running the banking API with or without Docker.

## Prerequisites
- Java 21
- Maven wrapper (`./mvnw`)
- Docker & Docker Compose (optional)

## Configuration
All runtime settings come from environment variables (see `.env.example`):
- DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
- JWT_SECRET, JWT_EXPIRATION
- SERVER_PORT

## Run with Docker
1. Copy `.env.example` to `.env` and adjust if needed.
2. Build image (runs tests): `./build.sh` (uses `IMAGE_TAG` env, default `bank-api:latest`).
3. Start stack: `./start.sh` (runs `docker compose up --build`).
4. API available at `http://localhost:${SERVER_PORT:-8080}`.

## Run without Docker
1. Ensure PostgreSQL is running with the same creds as `.env.example`.
2. Export env vars (or rely on defaults) and start the app:
   ```bash
   export DB_HOST=localhost DB_PORT=5432 DB_NAME=bankdb DB_USER=banking-kata DB_PASSWORD=banking-kata-pass
   export JWT_SECRET=mySecretKeyForJWTTokenGenerationThatIsLongEnough1234567890
   export SERVER_PORT=8080
   ./mvnw -pl banking-app spring-boot:run
   ```
3. Run tests (uses H2): `./mvnw clean test`.

## Scripts
- `build.sh` — builds the Docker image (uses `IMAGE_TAG` env).
- `start.sh` — starts the app + Postgres via Docker Compose, building if necessary.

## Notes
- Docker Compose auto-loads `.env`.
- Defaults are safe for local dev; change secrets for any non-local use.
