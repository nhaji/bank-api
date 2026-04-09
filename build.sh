#!/usr/bin/env bash
set -euo pipefail

IMAGE_TAG=${IMAGE_TAG:-bank-api:latest}

echo "Building Docker image: ${IMAGE_TAG}" 
docker build -t "${IMAGE_TAG}" .
