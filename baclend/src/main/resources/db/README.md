# Database Setup

This directory contains database setup and backup files.

## Quick Start for New Developers

### Option 1: Restore from SQL Dump (Recommended)

Use the provided dump file to quickly populate your database without running the full sync:

**On Windows (PowerShell):**
```powershell
mysql -h localhost -u root -p groupproject < groupproject_dump.sql
# Enter password: root
```

**On Mac/Linux (Bash):**
```bash
mysql -h localhost -u root -p groupproject < groupproject_dump.sql
# Enter password: root
```

**Or using MySQL Workbench:**
1. Open MySQL Workbench
2. Go to `Server` → `Data Import`
3. Select `Import from Self-Contained File`
4. Browse to this `groupproject_dump.sql` file
5. Click `Start Import`

### Option 2: Run Sync (if you need fresh data from Congress API)

If you prefer to sync fresh data from the Congress API instead:

1. Start the Spring Boot application
2. Make a POST request to trigger the sync:
   ```bash
   curl -X POST http://localhost:8080/api/members/sync
   curl -X POST http://localhost:8080/api/legislation/sync
   curl -X POST http://localhost:8080/api/bill-summaries/sync
   ```

## Database Tables

The dump includes:

- **members**: Congressional members (current and historical)
- **legislation**: Bills and resolutions (top-5 sponsored/cosponsored per member)
- **member_legislation**: Join table linking members to their legislation
- **bill_summaries**: Text summaries for bills (from Congress API)

## Regenerating the Dump

If you need to regenerate the dump with updated data:

```bash
python generate_db_dump.py
```

This script will connect to your local MySQL database and create a fresh dump file.

## Prerequisites

- MySQL Server running on `localhost:3306`
- Database credentials: `root:root` (matches application.properties)
- Python 3.7+ (only if regenerating the dump)
