import os
import shutil
import sqlite3
from pathlib import Path

# Resolve database path relative to project root
BASE_DIR = Path(__file__).resolve().parent.parent

# In Vercel serverless environment, files outside /tmp are read-only
if os.environ.get("VERCEL"):
    DB_PATH = Path("/tmp/fest.db")
    source_db = BASE_DIR / "fest.db"
    if not DB_PATH.exists() and source_db.exists():
        try:
            shutil.copy2(source_db, DB_PATH)
        except Exception as e:
            print(f"Notice: could not copy fest.db to /tmp: {e}")
else:
    DB_PATH = BASE_DIR / "fest.db"


def get_db_connection():
    """Return a SQLite connection with dict-like row access and foreign key enforcement."""
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    conn.execute("PRAGMA foreign_keys = ON")
    return conn


def init_db():
    """Initialize database tables for events and registrations."""
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS events (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        title TEXT NOT NULL,
        category TEXT NOT NULL,
        description TEXT,
        event_date TEXT NOT NULL,
        event_time TEXT NOT NULL,
        venue TEXT NOT NULL,
        capacity INTEGER NOT NULL,
        fee INTEGER NOT NULL DEFAULT 0,
        icon TEXT
    )
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS registrations (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        event_id INTEGER NOT NULL REFERENCES events(id),
        student_name TEXT NOT NULL,
        roll_number TEXT NOT NULL,
        email TEXT NOT NULL,
        phone TEXT,
        registered_at TEXT NOT NULL,
        UNIQUE(event_id, email)
    )
    """)

    conn.commit()
    conn.close()


if __name__ == "__main__":
    init_db()
    print("Database tables initialized successfully at:", DB_PATH)
