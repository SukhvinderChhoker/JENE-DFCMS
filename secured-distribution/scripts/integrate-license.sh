#!/bin/bash
# ============================================================
# JENE-DFCMS - Integration Script
# Copies source code and integrates license system into backend
# ============================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
DIST_DIR="$SCRIPT_DIR/.."
BACKEND_SRC="$PROJECT_ROOT/hwt-backend"
FRONTEND_SRC="$PROJECT_ROOT/hwt-frontend"
BACKEND_DIST="$DIST_DIR/hwt-backend"
FRONTEND_DIST="$DIST_DIR/hwt-frontend"

echo "=========================================="
echo "  Integrating License System"
echo "=========================================="
echo "Project Root:    $PROJECT_ROOT"
echo "Distribution:    $DIST_DIR"
echo ""

# --- Copy backend source ---
echo "[1/5] Copying backend source..."
rm -rf "$BACKEND_DIST"
cp -r "$BACKEND_SRC" "$BACKEND_DIST"

# --- Copy frontend source ---
echo "[2/5] Copying frontend source..."
rm -rf "$FRONTEND_DIST"
cp -r "$FRONTEND_SRC" "$FRONTEND_DIST"

# --- Inject license Java classes ---
echo "[3/5] Injecting license validation classes..."
LICENSE_JAVA="$BACKEND_DIST/src/main/java/com/foreman/license"
mkdir -p "$LICENSE_JAVA"
cp "$DIST_DIR/license-system/java-classes/model/LicenseData.java" "$LICENSE_JAVA/"
cp "$DIST_DIR/license-system/java-classes/model/LicenseFile.java" "$LICENSE_JAVA/"
cp "$DIST_DIR/license-system/java-classes/config/LicenseValidator.java" "$LICENSE_JAVA/"
cp "$DIST_DIR/license-system/java-classes/config/LicenseValidationResult.java" "$LICENSE_JAVA/"
cp "$DIST_DIR/license-system/java-classes/config/LicenseFilter.java" "$LICENSE_JAVA/"
cp "$DIST_DIR/license-system/java-classes/config/LicenseRunListener.java" "$LICENSE_JAVA/"

# --- Inject Spring factories ---
echo "[4/5] Injecting Spring factories..."
mkdir -p "$BACKEND_DIST/src/main/resources/META-INF/spring"
cp "$DIST_DIR/license-system/java-classes/META-INF/spring/org.springframework.boot.SpringApplicationRunListener" \
   "$BACKEND_DIST/src/main/resources/META-INF/spring/"

# --- Inject public key into classpath ---
echo "[5/5] Embedding public key..."
mkdir -p "$BACKEND_DIST/src/main/resources/license"
cp "$DIST_DIR/license-system/keys/public_key.pem" "$BACKEND_DIST/src/main/resources/license/"

# --- Update application.properties ---
PROPS_FILE="$BACKEND_DIST/src/main/resources/application.properties"
if ! grep -q "license.file" "$PROPS_FILE"; then
    echo "" >> "$PROPS_FILE"
    echo "# License Configuration" >> "$PROPS_FILE"
    echo "license.file=license/jene-dfcms.license" >> "$PROPS_FILE"
    echo "license.required=true" >> "$PROPS_FILE"
fi

# --- Update prod application properties ---
PROD_PROPS="$BACKEND_DIST/src/main/resources/application-prod.properties"
if [ -f "$PROD_PROPS" ] && ! grep -q "license.file" "$PROD_PROPS"; then
    echo "" >> "$PROD_PROPS"
    echo "# License Configuration" >> "$PROD_PROPS"
    echo "license.file=\${LICENSE_FILE:license/jene-dfcms.license}" >> "$PROD_PROPS"
    echo "license.required=\${LICENSE_REQUIRED:true}" >> "$PROD_PROPS"
fi

# --- Copy Dockerfiles ---
cp "$DIST_DIR/docker/hwt-backend/Dockerfile.prod" "$BACKEND_DIST/Dockerfile.prod"
cp "$DIST_DIR/docker/hwt-frontend/Dockerfile.prod" "$FRONTEND_DIST/Dockerfile.prod"
cp "$DIST_DIR/docker/hwt-frontend/nginx.prod.conf" "$FRONTEND_DIST/nginx.prod.conf"

echo ""
echo "=========================================="
echo "  Integration Complete!"
echo "=========================================="
echo "  Backend:  $BACKEND_DIST"
echo "  Frontend: $FRONTEND_DIST"
echo ""
echo "  Next steps:"
echo "  1. cd $DIST_DIR"
echo "  2. Generate license: python license-system/generate_license.py --customer 'Name' --type trial"
echo "  3. Copy license to docker volume or mount path"
echo "  4. cd docker && docker-compose -f docker-compose.prod.yml up -d --build"
echo "=========================================="
