@echo off
REM Database restoration script for Windows
REM This script restores the groupproject database from the SQL dump

setlocal enabledelayedexpansion

echo.
echo ============================================================
echo  GroupProject - Database Restoration
echo ============================================================
echo.

set "DUMP_FILE=%~dp0\groupproject_dump.sql"
set "MYSQL_USER=root"
set "MYSQL_PASS=root"
set "MYSQL_HOST=localhost"
set "MYSQL_DB=groupproject"

if not exist "%DUMP_FILE%" (
    echo ERROR: Dump file not found at %DUMP_FILE%
    echo Please ensure groupproject_dump.sql is in the same directory as this script.
    pause
    exit /b 1
)

echo [*] Checking MySQL connection...
mysql -h %MYSQL_HOST% -u %MYSQL_USER% -p%MYSQL_PASS% -e "SELECT 1" >nul 2>&1

if errorlevel 1 (
    echo ERROR: Cannot connect to MySQL. Please ensure:
    echo   - MySQL Server is running
    echo   - Host: %MYSQL_HOST%
    echo   - User: %MYSQL_USER%
    echo   - Password is correct
    pause
    exit /b 1
)

echo [✓] MySQL connection successful
echo [*] Dropping existing database (if exists)...
mysql -h %MYSQL_HOST% -u %MYSQL_USER% -p%MYSQL_PASS% -e "DROP DATABASE IF EXISTS %MYSQL_DB%;"

if errorlevel 1 (
    echo ERROR: Failed to drop database
    pause
    exit /b 1
)

echo [*] Creating fresh database...
mysql -h %MYSQL_HOST% -u %MYSQL_USER% -p%MYSQL_PASS% -e "CREATE DATABASE %MYSQL_DB%;"

if errorlevel 1 (
    echo ERROR: Failed to create database
    pause
    exit /b 1
)

echo [*] Restoring database from dump...
mysql -h %MYSQL_HOST% -u %MYSQL_USER% -p%MYSQL_PASS% %MYSQL_DB% < "%DUMP_FILE%"

if errorlevel 1 (
    echo ERROR: Failed to restore database
    pause
    exit /b 1
)

echo.
echo ============================================================
echo [✓] SUCCESS: Database restored successfully!
echo ============================================================
echo.
echo Database: %MYSQL_DB%
echo Location: %MYSQL_HOST%
echo User:     %MYSQL_USER%
echo.
echo You can now start the application and it will connect to
echo the restored database with all data preloaded.
echo.
pause
