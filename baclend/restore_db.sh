#!/bin/bash
# Database restoration script for macOS/Linux
# This script restores the groupproject database from the SQL dump

set -e

echo ""
echo "============================================================"
echo "  GroupProject - Database Restoration"
echo "============================================================"
echo ""

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DUMP_FILE="$SCRIPT_DIR/src/main/resources/db/groupproject_dump.sql"
MYSQL_USER="root"
MYSQL_PASS="root"
MYSQL_HOST="localhost"
MYSQL_DB="groupproject"

# Check if dump file exists
if [ ! -f "$DUMP_FILE" ]; then
    echo "❌ ERROR: Dump file not found at $DUMP_FILE"
    echo "Please ensure groupproject_dump.sql is in the expected location."
    exit 1
fi

echo "[*] Dump file: $DUMP_FILE"

# Check MySQL connection
echo "[*] Checking MySQL connection..."
if ! mysql -h "$MYSQL_HOST" -u "$MYSQL_USER" -p"$MYSQL_PASS" -e "SELECT 1" > /dev/null 2>&1; then
    echo "❌ ERROR: Cannot connect to MySQL. Please ensure:"
    echo "   - MySQL Server is running"
    echo "   - Host: $MYSQL_HOST"
    echo "   - User: $MYSQL_USER"
    echo "   - Password is correct"
    echo ""
    echo "   Try starting MySQL:"
    echo "   macOS:  brew services start mysql-community-server"
    echo "   Linux:  sudo systemctl start mysql"
    exit 1
fi

echo "✓ MySQL connection successful"

# Drop existing database
echo "[*] Dropping existing database (if exists)..."
mysql -h "$MYSQL_HOST" -u "$MYSQL_USER" -p"$MYSQL_PASS" -e "DROP DATABASE IF EXISTS \`$MYSQL_DB\`;" 2>/dev/null || true

# Create fresh database
echo "[*] Creating fresh database..."
mysql -h "$MYSQL_HOST" -u "$MYSQL_USER" -p"$MYSQL_PASS" -e "CREATE DATABASE \`$MYSQL_DB\`;" 2>/dev/null

# Restore from dump
echo "[*] Restoring database from dump (this may take a moment)..."
mysql -h "$MYSQL_HOST" -u "$MYSQL_USER" -p"$MYSQL_PASS" "$MYSQL_DB" < "$DUMP_FILE"

if [ $? -ne 0 ]; then
    echo "❌ ERROR: Failed to restore database"
    exit 1
fi

echo ""
echo "============================================================"
echo " ✅ SUCCESS: Database restored successfully!"
echo "============================================================"
echo ""
echo "Database: $MYSQL_DB"
echo "Location: $MYSQL_HOST"
echo "User:     $MYSQL_USER"
echo ""
echo "You can now start the application and it will connect to"
echo "the restored database with all data preloaded."
echo ""
