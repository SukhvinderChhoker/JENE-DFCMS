@echo off
REM ============================================================
REM JENE-DFCMS - Build, Tag & Push to Docker Hub
REM ============================================================
REM Prerequisites:
REM   1. Docker Desktop running
REM   2. Logged in: docker login
REM   3. .env file configured in docker/ folder
REM ============================================================

setlocal enabledelayedexpansion

echo ==========================================
echo   JENE-DFCMS Secured Docker Build
echo ==========================================

REM --- Load .env ---
if not exist "docker\.env" (
    echo ERROR: docker\.env not found. Copy .env.example to .env and configure.
    exit /b 1
)

for /f "tokens=1,* delims==" %%a in ('type docker\.env') do (
    set "%%a=%%b"
)

if "%DOCKER_REGISTRY%"=="" (
    echo ERROR: DOCKER_REGISTRY not set in docker\.env
    exit /b 1
)
if "%IMAGE_NAME%"=="" set IMAGE_NAME=jene-dfcms
if "%IMAGE_TAG%"=="" set IMAGE_TAG=1.0.0

set FULL_IMAGE=%DOCKER_REGISTRY%/%IMAGE_NAME%
set TAGGED=%FULL_IMAGE%:%IMAGE_TAG%
set LATEST=%FULL_IMAGE%:latest

echo.
echo Registry:  %DOCKER_REGISTRY%
echo Image:     %TAGGED%
echo Latest:    %LATEST%
echo.

REM --- Build Backend ---
echo [1/4] Building backend image...
docker build -f docker\hwt-backend\Dockerfile.prod -t %TAGGED%-backend -t %LATEST%-backend .
if errorlevel 1 (
    echo ERROR: Backend build failed
    exit /b 1
)

REM --- Build Frontend ---
echo [2/4] Building frontend image...
docker build -f docker\hwt-frontend\Dockerfile.prod -t %TAGGED%-frontend -t %LATEST%-frontend .
if errorlevel 1 (
    echo ERROR: Frontend build failed
    exit /b 1
)

REM --- Tag ---
echo [3/4] Tagging images...
docker tag %TAGGED%-backend %TAGGED%-backend
docker tag %TAGGED%-frontend %TAGGED%-frontend

REM --- Push ---
echo [4/4] Pushing to Docker Hub...
echo Make sure you are logged in: docker login
docker push %TAGGED%-backend
docker push %TAGGED%-frontend
docker push %LATEST%-backend
docker push %LATEST%-frontend

echo.
echo ==========================================
echo   Build and Push Complete!
echo ==========================================
echo   Backend: %TAGGED%-backend
echo   Frontend: %TAGGED%-frontend
echo ==========================================

endlocal
