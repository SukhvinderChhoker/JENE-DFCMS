# JENE-DFCMS Deployment Guide

## Prerequisites

- Docker Desktop 4.x+ or Docker Engine 24.x+
- Docker Compose v2.x+
- Python 3.8+ (for license generation)
- Git (for source access)

## Quick Start

### Step 1: Integrate License System

```bash
cd secured-distribution

# Linux/Mac
bash scripts/integrate-license.sh

# Windows
scripts\integrate-license.bat
```

### Step 2: Generate License

```bash
# Trial license (20 days)
python license-system/generate_license.py \
    --customer "Your Company Name" \
    --type trial

# Annual license (365 days)
python license-system/generate_license.py \
    --customer "Your Company Name" \
    --type annual
```

### Step 3: Configure Environment

```bash
cd docker
cp .env.example .env
```

Edit `.env` with your values:
- `DB_PASSWORD`: Strong database password
- `JWT_SECRET`: Random 64+ character string
- `CORS_ORIGIN`: Your domain (e.g., `https://app.example.com`)

### Step 4: Deploy

```bash
cd docker

# Build and start
docker-compose -f docker-compose.prod.yml up -d --build

# Check status
docker-compose -f docker-compose.prod.yml ps

# View logs
docker-compose -f docker-compose.prod.yml logs -f backend
```

### Step 5: Verify

```bash
# Test license validation
curl http://localhost:8080/api/auth/login
# Should return 503 if no valid license

# After placing license file:
curl http://localhost:8080/api/auth/login
# Should return login form (200 OK)
```

## License Deployment

### Option A: Volume Mount

```yaml
# In docker-compose.prod.yml, under backend.volumes:
volumes:
  - /path/to/licenses:/app/license:ro
```

### Option B: Copy into Container

```bash
docker cp jene-dfcms.license jene-secured-api:/app/license/jene-dfcms.license
docker restart jene-secured-api
```

### Option C: Build-time Embedding

Place license file in `docker/license/` before building. It will be included in the image.

## Building Docker Images

### Local Build

```bash
cd secured-distribution/docker

# Build all
docker-compose -f docker-compose.prod.yml build

# Build specific service
docker-compose -f docker-compose.prod.yml build backend
docker-compose -f docker-compose.prod.yml build frontend
```

### Push to Docker Hub

```bash
# Set your registry in .env
DOCKER_REGISTRY=your-dockerhub-username

# Build and push
scripts/build-and-push.sh        # Linux/Mac
scripts\build-and-push.bat       # Windows
```

### Push to Private Registry

```bash
# Login
docker login registry.example.com

# Tag
docker tag jene-secured-api:latest registry.example.com/jene-secured-backend:1.0.0
docker tag jene-secured-web:latest registry.example.com/jene-secured-frontend:1.0.0

# Push
docker push registry.example.com/jene-secured-backend:1.0.0
docker push registry.example.com/jene-secured-frontend:1.0.0
```

## Updating License

```bash
# Generate new license
python license-system/generate_license.py --customer "New Name" --type annual

# Copy to server
cp licenses/jene-dfcms-annual-*.license server:/path/to/license/jene-dfcms.license

# Restart backend
docker-compose -f docker-compose.prod.yml restart backend
```

## Troubleshooting

### "LICENSE INVALID" error
1. Check license file exists at the path configured in `LICENSE_FILE`
2. Verify file is not corrupted
3. Check system time is correct (expiry validation)
4. Verify public key matches private key used to sign

### Container won't start
```bash
docker logs jene-secured-api
```
Look for license validation errors in logs.

### Cannot connect to database
```bash
docker-compose -f docker-compose.prod.yml logs postgres
```
Ensure PostgreSQL is healthy before backend starts.

### Frontend can't reach backend
Check that backend container is healthy:
```bash
docker-compose -f docker-compose.prod.yml ps
```

## Security Notes

- **Never commit `.env` files** to version control
- **Never share the private key** (`private_key.pem`)
- **Keep license files secure** - they are transferable
- **Monitor logs** for repeated license validation failures
- **Use Docker secrets** for production deployments when possible

## Folder Structure After Integration

```
secured-distribution/
├── docker/
│   ├── .env.example
│   ├── docker-compose.prod.yml
│   ├── hwt-backend/
│   │   └── Dockerfile.prod
│   └── hwt-frontend/
│       ├── Dockerfile.prod
│       └── nginx.prod.conf
├── hwt-backend/          (after integration)
├── hwt-frontend/         (after integration)
├── license-system/
│   ├── generate_license.py
│   ├── keys/
│   │   ├── private_key.pem    (KEEP SECRET)
│   │   └── public_key.pem     (embedded in JAR)
│   └── licenses/
│       └── *.license
├── scripts/
│   ├── integrate-license.sh
│   ├── integrate-license.bat
│   ├── build-and-push.sh
│   └── build-and-push.bat
├── SECURITY-IMPLEMENTATION-PLAN.md
└── DEPLOYMENT-GUIDE.md
```
