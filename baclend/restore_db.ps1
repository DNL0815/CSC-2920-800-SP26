#!/usr/bin/env powershell
<#
.SYNOPSIS
    Restore GroupProject database from SQL dump
.DESCRIPTION
    This script restores the groupproject database from the provided SQL dump file.
    Perfect for new developers to get up and running quickly.
#>

param(
    [string]$DumpFile = "src\main\resources\db\groupproject_dump.sql",
    [string]$MySqlUser = "root",
    [string]$MySqlPass = "root",
    [string]$MySqlHost = "localhost",
    [string]$MySqlDb = "groupproject"
)

$ErrorActionPreference = "Stop"

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host " GroupProject - Database Restoration (PowerShell)" -ForegroundColor Cyan
Write-Host "============================================================`n" -ForegroundColor Cyan

# Resolve full path
$DumpFilePath = Join-Path $PSScriptRoot $DumpFile
if (-not (Test-Path $DumpFilePath)) {
    Write-Host "❌ ERROR: Dump file not found at: $DumpFilePath" -ForegroundColor Red
    exit 1
}

Write-Host "📂 Dump file: $DumpFilePath" -ForegroundColor Green

# Check MySQL connection
Write-Host "[*] Checking MySQL connection..." -ForegroundColor Yellow
try {
    $testCmd = "mysql -h $MySqlHost -u $MySqlUser -p$MySqlPass -e `"SELECT 1`" 2>$null"
    Invoke-Expression $testCmd | Out-Null
    Write-Host "✓ MySQL connection successful" -ForegroundColor Green
} catch {
    Write-Host "❌ ERROR: Cannot connect to MySQL at $MySqlHost" -ForegroundColor Red
    Write-Host "   Please ensure:" -ForegroundColor Red
    Write-Host "   - MySQL Server is running" -ForegroundColor Red
    Write-Host "   - Host: $MySqlHost" -ForegroundColor Red
    Write-Host "   - User: $MySqlUser" -ForegroundColor Red
    exit 1
}

# Drop existing database
Write-Host "[*] Dropping existing database (if exists)..." -ForegroundColor Yellow
$dropCmd = "mysql -h $MySqlHost -u $MySqlUser -p$MySqlPass -e `"DROP DATABASE IF EXISTS \`\`$MySqlDb\`\`;`""
Invoke-Expression $dropCmd 2>&1 | Out-Null

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERROR: Failed to drop database" -ForegroundColor Red
    exit 1
}

# Create fresh database
Write-Host "[*] Creating fresh database..." -ForegroundColor Yellow
$createCmd = "mysql -h $MySqlHost -u $MySqlUser -p$MySqlPass -e `"CREATE DATABASE \`\`$MySqlDb\`\`;`""
Invoke-Expression $createCmd 2>&1 | Out-Null

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERROR: Failed to create database" -ForegroundColor Red
    exit 1
}

# Restore from dump
Write-Host "[*] Restoring database from dump (this may take a moment)..." -ForegroundColor Yellow
$restoreCmd = "mysql -h $MySqlHost -u $MySqlUser -p$MySqlPass $MySqlDb < `"$DumpFilePath`""
Invoke-Expression $restoreCmd 2>&1 | Out-Null

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERROR: Failed to restore database" -ForegroundColor Red
    exit 1
}

Write-Host "`n============================================================" -ForegroundColor Green
Write-Host " ✅ SUCCESS: Database restored successfully!" -ForegroundColor Green
Write-Host "============================================================`n" -ForegroundColor Green

Write-Host "Database: $MySqlDb" -ForegroundColor Cyan
Write-Host "Location: $MySqlHost" -ForegroundColor Cyan
Write-Host "User:     $MySqlUser" -ForegroundColor Cyan
Write-Host "`nYou can now start the application and it will connect to" -ForegroundColor Yellow
Write-Host "the restored database with all data preloaded.`n" -ForegroundColor Yellow
