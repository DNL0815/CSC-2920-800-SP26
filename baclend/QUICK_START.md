# Quick Reference: SQL Dump & Restore

## For New Developers

### 🚀 Get Started (5 minutes)

```powershell
# 1. Clone & navigate
git clone <repo>
cd baclend

# 2. Restore database (Windows - PowerShell)
.\restore_db.ps1

# 3. Build & run
mvn clean install
mvn spring-boot:run

# ✅ Done! Visit http://localhost:8080
```

### 🔧 Alternative Restore Commands

**Windows - Command Prompt:**
```cmd
restore_db.bat
```

**macOS/Linux:**
```bash
bash restore_db.sh
```

**Direct MySQL (any OS):**
```bash
mysql -u root -p groupproject < src/main/resources/db/groupproject_dump.sql
```

## For Maintainers

### 📦 Update the Dump

When database changes (new sync runs):

```bash
python generate_db_dump.py
```

Then commit to repo:
```bash
git add src/main/resources/db/groupproject_dump.sql
git commit -m "Update database dump with latest data"
git push
```

### 🔍 Verify Dump

Check dump statistics:
```bash
# Count INSERT statements
grep -c "INSERT INTO" src/main/resources/db/groupproject_dump.sql

# View first few rows
head -20 src/main/resources/db/groupproject_dump.sql

# Get file size
ls -lh src/main/resources/db/groupproject_dump.sql
```

## 📋 What's Included

- **members**: 538 current Congressional members
- **legislation**: ~2,819 bills (top-5 per member)
- **member_legislation**: Links between members & bills
- **bill_summaries**: ~325 bill text summaries

## 🆘 Troubleshooting

**Can't connect to MySQL:**
- macOS: `brew services start mysql-community-server`
- Windows: Start MySQL from Services or Workbench
- Linux: `sudo systemctl start mysql`

**Permission denied (shell script):**
```bash
chmod +x restore_db.sh
./restore_db.sh
```

**Port 8080 already in use:**
- Edit `application.properties`: change `server.port=8081`

## 📚 Full Docs

See `SETUP.md` for comprehensive setup guide.

---

**Files Location:**
- Dump: `src/main/resources/db/groupproject_dump.sql`
- Restore Scripts: `restore_db.{ps1|bat|sh}`
- Generator: `generate_db_dump.py`
- Docs: `SETUP.md`, `src/main/resources/db/README.md`
