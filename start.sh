#!/usr/bin/env bash
set -euo pipefail

# Uses docker-compose to build (if needed) and start services.
# .env is loaded automatically by docker compose.

docker compose up --build
