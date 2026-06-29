@echo off
REM ============================================================
REM JENE-DFCMS - Integration Script (Windows)
REM Copies source code and integrates license system into backend
REM ============================================================

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set DIST_DIR=%SCRIPT_DIR%..
set PROJECT_ROOT=%DIST_DIR%\..
set BACKEND_SRC=%PROJECT_ROOT%\hwt-backend
set FRONTEND_SRC=%PROJECT_ROOT%\hwt-frontend
set BACKEND_DIST=%DIST_DIR%\hwt-backend
set FRONTEND_DIST=%DIST_DIR%\hwt-frontend

echo ==========================================
echo   Integrating License System
echo ==========================================
echo Project Root:    %PROJECT_ROOT%
echo Distribution:    %DIST_DIR%
echo.

echo [1/5] Copying backend source...
if exist "%BACKEND_DIST%" rmdir /s /q "%BACKEND_DIST%"
xcopy "%BACKEND_SRC%" "%BACKEND_DIST%\" /E /I /Q /Y >nul

echo [2/5] Copying frontend source...
if exist "%FRONTEND_DIST%" rmdir /s /q "%FRONTEND_DIST%"
xcopy "%FRONTEND_SRC%" "%FRONTEND_DIST%\" /E /I /Q /Y >nul

echo [3/5] Injecting license validation classes...
set LICENSE_JAVA=%BACKEND_DIST%\src\main\java\com\foreman\license
mkdir "%LICENSE_JAVA%" 2>nul
copy /y "%DIST_DIR%\license-system\java-classes\model\LicenseData.java" "%LICENSE_JAVA%\" >nul
copy /y "%DIST_DIR%\license-system\java-classes\model\LicenseFile.java" "%LICENSE_JAVA%\" >nul
copy /y "%DIST_DIR%\license-system\java-classes\config\LicenseValidator.java" "%LICENSE_JAVA%\" >nul
copy /y "%DIST_DIR%\license-system\java-classes\config\LicenseValidationResult.java" "%LICENSE_JAVA%\" >nul
copy /y "%DIST_DIR%\license-system\java-classes\config\LicenseFilter.java" "%LICENSE_JAVA%\" >nul
copy /y "%DIST_DIR%\license-system\java-classes\config\LicenseRunListener.java" "%LICENSE_JAVA%\" >nul

echo [4/5] Injecting Spring factories...
set SPRING_META=%BACKEND_DIST%\src\main\resources\META-INF\spring
mkdir "%SPRING_META%" 2>nul
copy /y "%DIST_DIR%\license-system\java-classes\META-INF\spring\org.springframework.boot.SpringApplicationRunListener" "%SPRING_META%\" >nul

echo [5/5] Embedding public key...
set LICENSE_RES=%BACKEND_DIST%\src\main\resources\license
mkdir "%LICENSE_RES%" 2>nul
copy /y "%DIST_DIR%\license-system\keys\public_key.pem" "%LICENSE_RES%\" >nul

REM Update application.properties
echo. >> "%BACKEND_DIST%\src\main\resources\application.properties"
echo # License Configuration >> "%BACKEND_DIST%\src\main\resources\application.properties"
echo license.file=license/jene-dfcms.license >> "%BACKEND_DIST%\src\main\resources\application.properties"
echo license.required=true >> "%BACKEND_DIST%\src\main\resources\application.properties"

REM Copy Dockerfiles
copy /y "%DIST_DIR%\docker\hwt-backend\Dockerfile.prod" "%BACKEND_DIST%\Dockerfile.prod" >nul
copy /y "%DIST_DIR%\docker\hwt-frontend\Dockerfile.prod" "%FRONTEND_DIST%\Dockerfile.prod" >nul
copy /y "%DIST_DIR%\docker\hwt-frontend\nginx.prod.conf" "%FRONTEND_DIST%\nginx.prod.conf" >nul

echo.
echo ==========================================
echo   Integration Complete!
echo ==========================================
echo   Backend:  %BACKEND_DIST%
echo   Frontend: %FRONTEND_DIST%
echo.
echo   Next steps:
echo   1. cd %DIST_DIR%
echo   2. python license-system\generate_license.py --customer "Name" --type trial
echo   3. cd docker ^&^& docker-compose -f docker-compose.prod.yml up -d --build
echo ==========================================

endlocal
