"""Root entrypoint for deployment platforms (Vercel, Render, Railway, etc.)"""
import sys
from pathlib import Path

# Ensure root directory is on Python path
sys.path.insert(0, str(Path(__file__).resolve().parent))

from backend.main import app

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("backend.main:app", host="0.0.0.0", port=8000, reload=True)
