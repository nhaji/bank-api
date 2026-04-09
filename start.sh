#!/usr/bin/env bash
set -euo pipefail

# Start services using existing images (build separately via ./build.sh).
docker compose up
