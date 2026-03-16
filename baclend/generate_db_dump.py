#!/usr/bin/env python3
"""
Generate SQL dump of groupproject database for easy dev setup.
Requires: pip install mysql-connector-python
"""

import mysql.connector
from mysql.connector import Error
import sys
from datetime import datetime

DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': 'root',
    'database': 'groupproject'
}

DUMP_FILE = 'src/main/resources/db/groupproject_dump.sql'

def generate_dump():
    """Generate SQL dump of the groupproject database."""
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        # Get all tables
        cursor.execute("""
            SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES 
            WHERE TABLE_SCHEMA = %s
        """, (DB_CONFIG['database'],))
        
        tables = [table[0] for table in cursor.fetchall()]
        
        if not tables:
            print("❌ No tables found in database!")
            return False
        
        print(f"📊 Found {len(tables)} tables: {', '.join(tables)}")
        
        # Generate dump content
        dump_lines = []
        dump_lines.append("-- ============================================================")
        dump_lines.append(f"-- SQL Dump of '{DB_CONFIG['database']}' database")
        dump_lines.append(f"-- Generated: {datetime.now().isoformat()}")
        dump_lines.append("-- ============================================================")
        dump_lines.append("")
        dump_lines.append("SET FOREIGN_KEY_CHECKS=0;")
        dump_lines.append("")
        
        # Dump each table
        for table in tables:
            print(f"  ⏳ Dumping table: {table}")
            
            # Get CREATE TABLE statement
            cursor.execute(f"SHOW CREATE TABLE `{table}`")
            create_stmt = cursor.fetchone()[1]
            dump_lines.append(f"-- ============================================================")
            dump_lines.append(f"-- Table: {table}")
            dump_lines.append(f"-- ============================================================")
            dump_lines.append(f"DROP TABLE IF EXISTS `{table}`;")
            dump_lines.append(create_stmt + ";")
            dump_lines.append("")
            
            # Get data from table
            cursor.execute(f"SELECT * FROM `{table}`")
            rows = cursor.fetchall()
            
            if rows:
                # Get column names
                cursor.execute(f"DESCRIBE `{table}`")
                columns = [col[0] for col in cursor.fetchall()]
                col_names = ", ".join([f"`{col}`" for col in columns])
                
                # Generate INSERT statements
                for row in rows:
                    values = []
                    for val in row:
                        if val is None:
                            values.append("NULL")
                        elif isinstance(val, bool):
                            values.append(str(int(val)))
                        elif isinstance(val, (int, float)):
                            values.append(str(val))
                        else:
                            # Escape single quotes
                            escaped = str(val).replace("'", "''")
                            values.append(f"'{escaped}'")
                    
                    insert_stmt = f"INSERT INTO `{table}` ({col_names}) VALUES ({', '.join(values)});"
                    dump_lines.append(insert_stmt)
                dump_lines.append("")
        
        dump_lines.append("SET FOREIGN_KEY_CHECKS=1;")
        dump_lines.append("")
        
        # Write to file
        with open(DUMP_FILE, 'w', encoding='utf-8') as f:
            f.write('\n'.join(dump_lines))
        
        print(f"\n✅ SQL dump created successfully: {DUMP_FILE}")
        print(f"📝 File size: {len('\n'.join(dump_lines)) / 1024:.2f} KB")
        return True
        
    except Error as e:
        print(f"❌ Database error: {e}")
        return False
    except Exception as e:
        print(f"❌ Error: {e}")
        return False
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

if __name__ == "__main__":
    import os
    
    # Create db directory if it doesn't exist
    os.makedirs('src/main/resources/db', exist_ok=True)
    
    success = generate_dump()
    sys.exit(0 if success else 1)
