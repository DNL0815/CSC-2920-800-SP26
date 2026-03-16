# 🚀 GroupProject - New Developer Setup Guide

Welcome! This guide will help you get the project running locally with the pre-populated database.

## Prerequisites

Make sure you have installed:

- **Java 17+** (Required for Spring Boot 3.2)
- **MySQL 5.7+** (Must be running on `localhost:3306`)
- **Maven 3.6+** (For building the project)
- **Git** (For cloning the repository)

Verify installations:
```bash
java -version
mysql --version
mvn --version
```

## Quick Setup (5 minutes)

### 1. Clone Repository
```bash
git clone <repository-url>
cd baclend
```

### 2. Restore Database from Dump

#### Windows (PowerShell - Recommended)
```powershell
.\restore_db.ps1
```

#### Windows (Command Prompt)
```cmd
restore_db.bat
```

#### Mac/Linux
```bash
mysql -h localhost -u root -p groupproject < src/main/resources/db/groupproject_dump.sql
# Password: root
```

**What this does:**
- Drops any existing `groupproject` database
- Creates a fresh `groupproject` database
- Loads all tables and data from the dump
- Takes about 10-30 seconds depending on disk speed

### 3. Build Project
```bash
mvn clean install
```

### 4. Run Application
```bash
mvn spring-boot:run
```

or from your IDE (IntelliJ, VS Code, etc.):
- Right-click `GroupProjectApplication.java`
- Select "Run"

### 5. Access Application
- **REST API**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health

## What's in the Database Dump?

The `groupproject_dump.sql` file contains:

### 📊 Tables

1. **members** (538 current U.S. Congressional members)
   - Name, party, state, chamber, bioguide ID, etc.
   - Only current members (as of last sync)

2. **legislation** (~2,819 bills and resolutions)
   - Top-5 sponsored/cosponsored bills per member
   - Congress 118-119
   - Bill types: HR, S, HRES, SRES, HCONRES, SCONRES, HJRES, SJRES

3. **member_legislation** (join table)
   - Links members to their legislation
   - ~2,819 associations
   - Sources: introduced, cosponsored

4. **bill_summaries** (~325 summaries)
   - Text summaries from Congress API
   - Multiple versions per bill (tracked by version_code)
   - Note: Not all bills have summaries available from the API

## Database Credentials

All environments use the same default credentials (configured in `application.properties`):

```
Host:     localhost:3306
Database: groupproject
User:     root
Password: root
```

⚠️ **Note:** These are development defaults. Change them before deploying to production.

## Useful API Endpoints

Once the application is running:

```bash
# Get all members
curl http://localhost:8080/api/members

# Get a specific member and their legislation
curl http://localhost:8080/api/members/{bioguideId}

# Get all legislation
curl http://localhost:8080/api/legislation

# Get bill summaries
curl http://localhost:8080/api/bill-summaries/{congress}/{billType}/{billNumber}/summaries
```

## Troubleshooting

### MySQL Connection Error
```
ERROR 2002: Can't connect to MySQL server on 'localhost'
```
**Solution:** Make sure MySQL is running
```bash
# macOS
brew services start mysql-community-server

# Windows (if installed via MSI)
net start MySQL80

# Or start MySQL Workbench
```

### Database Already Exists Error
The restore script automatically drops the existing database. If you get an error:
```bash
# Manually drop it
mysql -u root -p -e "DROP DATABASE IF EXISTS groupproject;"
# Then try restore again
```

### Port Already in Use (8080)
If Spring Boot won't start on port 8080, either:
1. Stop other applications using that port
2. Change the port in `application.properties`:
   ```properties
   server.port=8081
   ```

### Out of Memory
If you get heap errors, increase Java memory:
```bash
export MAVEN_OPTS="-Xmx1024m"
mvn spring-boot:run
```

## Regenerating the Database Dump

If you need to create an updated dump with new data:

```bash
python generate_db_dump.py
```

**Requirements:**
- Python 3.7+
- `pip install mysql-connector-python`

This will create a fresh `src/main/resources/db/groupproject_dump.sql` with your current database data.

## Next Steps

1. **Read the Code:** Start with `src/main/java/com/csc2920/group_project/GroupProjectApplication.java`
2. **Explore APIs:** Check the controller classes in `src/main/java/com/csc2920/group_project/controller/`
3. **Understand Data:** Review the entity classes in `src/main/java/com/csc2920/group_project/entity/`
4. **Run Tests:** `mvn test`

## Getting Help

- Check the main `README.md` for project overview
- Review `HELP.md` for Spring Boot questions
- Look at test files in `src/test/` for usage examples

## Tips

✅ **Make sure MySQL is running before starting the app**
✅ **Run the restore script with admin privileges if you get permission errors**
✅ **Keep `generate_db_dump.py` in the project root for easy regeneration**
✅ **Add the dump file to `.gitignore` if it gets too large (optional)**

---

**Last Updated:** 2026-03-16
**Dump Size:** ~2.5 MB
**Database Size:** ~5 MB (after restore)

Good luck! 🎉
