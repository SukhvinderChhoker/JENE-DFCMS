#!/bin/bash
# ============================================================
# JENE-DFCMS - Build, Tag & Push to Docker Hub
# ============================================================
# Prerequisites:
#   1. Docker Desktop running
#   2. Logged in: docker login
#   3. .env file configured in docker/ folder
# ============================================================

set -e

echo "=========================================="
echo "  JENE-DFCMS Secured Docker Build"
echo "=========================================="

# Load .env
if [ ! -f "docker/.env" ]; then
    echo "ERROR: docker/.env not found. Copy .env.example to .env and configure."
    exit 1
fi

export $(grep -v '^#' docker/.env | xargs)

DOCKER_REGISTRY=${DOCKER_REGISTRY:?"Set DOCKER_REGISTRY in docker/.env"}
IMAGE_NAME=${IMAGE_NAME:-jene-dfcms}
IMAGE_TAG=${IMAGE_TAG:-1.0.0}

FULL_IMAGE="$DOCKER_REGISTRY/$IMAGE_NAME"
TAGGED="$FULL_IMAGE:$IMAGE_TAG"
LATEST="$FULL_IMAGE:latest"

echo ""
echo "Registry:  $DOCKER_REGISTRY"
echo "Image:     $TAGGED"
echo "Latest:    $LATEST"
echo ""

# Build Backend
echo "[1/4] Building backend image..."
docker build -f docker/hwt-backend/Dockerfile.prod \
    -t "$TAGGED-backend" \
    -t "$LATEST-backend" .

# Build Frontend
echo "[2/4] Building frontend image..."
docker build -f docker/hwt-frontend/Dockerfile.prod \
    -t "$TAGGED-frontend" \
    -t "$LATEST-frontend" .

# Push
echo "[3/4] Pushing to Docker Hub..."
echo "Make sure you are logged in: docker login"
docker push "$TAGGED-backend"
docker push "$TAGGED-frontend"
docker push "$LATEST-backend"
docker push "$LATEST-frontend"

echo ""
echo "=========================================="
echo "  Build and Push Complete!"
echo "=========================================="
echo "  Backend:  $TAGGED-backend"
echo "  Frontend: $TAGGED-frontend"
echo "=========================================="
