import os
import sqlite3
from contextlib import asynccontextmanager
from datetime import date, datetime
from pathlib import Path
from typing import List, Optional

from fastapi import FastAPI, HTTPException, Query, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles

from backend.database import get_db_connection, init_db
from backend.models import (
    EventCreateIn,
    EventDetail,
    EventOut,
    RegistrationDetail,
    RegistrationIn,
    RegistrationOut,
    StatsOut,
)
from backend.seed_data import seed_events

BASE_DIR = Path(__file__).resolve().parent.parent
FRONTEND_DIR = BASE_DIR / "frontend"


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Lifespan event: initialize database schema and seed data on startup."""
    init_db()
    try:
        seed_events()
    except Exception as e:
        print(f"Startup notice regarding seeding: {e}")
    yield


app = FastAPI(
    title="Zenith '26 College Fest Management System API",
    description="Backend API powering student registration and coordinator admin dashboard for Zenith '26",
    version="1.0.0",
    lifespan=lifespan,
)

# Enable CORS for local cross-origin development (including Android emulator / device)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ==========================================
# API ENDPOINTS
# ==========================================

@app.get("/api/events", response_model=List[EventOut], summary="List all events with live seat counts")
def get_events(
    category: Optional[str] = Query(None, description="Filter by category (Technical, Cultural, Sports, Gaming, Workshop)"),
    search: Optional[str] = Query(None, description="Search term matching title (case-insensitive)"),
):
    """Returns a list of fest events, each with available_seats computed live."""
    conn = get_db_connection()
    cursor = conn.cursor()

    query = """
        SELECT 
            e.id, e.title, e.category, e.description, e.event_date, e.event_time,
            e.venue, e.capacity, e.fee, e.icon,
            (e.capacity - COALESCE(r.reg_count, 0)) AS available_seats
        FROM events e
        LEFT JOIN (
            SELECT event_id, COUNT(*) AS reg_count
            FROM registrations
            GROUP BY event_id
        ) r ON e.id = r.event_id
        WHERE 1=1
    """
    params = []

    if category and category.strip().lower() != "all":
        query += " AND LOWER(e.category) = LOWER(?)"
        params.append(category.strip())

    if search and search.strip():
        query += " AND LOWER(e.title) LIKE LOWER(?)"
        params.append(f"%{search.strip()}%")

    query += " ORDER BY e.id ASC"

    cursor.execute(query, params)
    rows = cursor.fetchall()
    conn.close()

    events = []
    for row in rows:
        events.append(
            EventOut(
                id=row["id"],
                title=row["title"],
                category=row["category"],
                description=row["description"],
                event_date=row["event_date"],
                event_time=row["event_time"],
                venue=row["venue"],
                capacity=row["capacity"],
                fee=row["fee"],
                icon=row["icon"],
                available_seats=max(0, row["available_seats"]),
            )
        )
    return events


@app.get("/api/events/{event_id}", response_model=EventDetail, summary="Get single event details")
def get_event_detail(event_id: int):
    """Returns details for a single event, or 404 if not found."""
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute(
        """
        SELECT 
            e.id, e.title, e.category, e.description, e.event_date, e.event_time,
            e.venue, e.capacity, e.fee, e.icon,
            (e.capacity - COALESCE(r.reg_count, 0)) AS available_seats
        FROM events e
        LEFT JOIN (
            SELECT event_id, COUNT(*) AS reg_count
            FROM registrations
            GROUP BY event_id
        ) r ON e.id = r.event_id
        WHERE e.id = ?
        """,
        (event_id,),
    )
    row = cursor.fetchone()
    conn.close()

    if not row:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Event with ID {event_id} not found",
        )

    return EventDetail(
        id=row["id"],
        title=row["title"],
        category=row["category"],
        description=row["description"],
        event_date=row["event_date"],
        event_time=row["event_time"],
        venue=row["venue"],
        capacity=row["capacity"],
        fee=row["fee"],
        icon=row["icon"],
        available_seats=max(0, row["available_seats"]),
    )


@app.post(
    "/api/events/{event_id}/register",
    response_model=RegistrationOut,
    status_code=status.HTTP_201_CREATED,
    summary="Register student for an event",
)
def register_student(event_id: int, payload: RegistrationIn):
    """
    Registers a student for the specified event with strict server-side validation:
    - Event must exist (404)
    - Name, Roll Number, and Email cannot be empty (400)
    - Email must contain '@' (400)
    - Event must not be at capacity (409)
    - Student email must not already be registered for this event (409)
    """
    # 1. Server-side field presence & format checks
    student_name = payload.student_name.strip() if payload.student_name else ""
    roll_number = payload.roll_number.strip() if payload.roll_number else ""
    email = payload.email.strip().lower() if payload.email else ""
    phone = payload.phone.strip() if payload.phone else None

    if not student_name:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Student name is required")
    if not roll_number:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Roll number is required")
    if not email:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Email is required")
    if "@" not in email or email.startswith("@") or email.endswith("@"):
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Invalid email address format")

    conn = get_db_connection()
    cursor = conn.cursor()

    # 2. Check event exists
    cursor.execute("SELECT id, title, capacity FROM events WHERE id = ?", (event_id,))
    event = cursor.fetchone()
    if not event:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Event with ID {event_id} not found",
        )

    # 3. Check capacity
    cursor.execute("SELECT COUNT(*) FROM registrations WHERE event_id = ?", (event_id,))
    current_regs = cursor.fetchone()[0]
    if current_regs >= event["capacity"]:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Event is full",
        )

    # 4. Check duplicate email registration for this event
    cursor.execute("SELECT id FROM registrations WHERE event_id = ? AND LOWER(email) = ?", (event_id, email))
    existing = cursor.fetchone()
    if existing:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Already registered",
        )

    # 5. Insert registration
    now_iso = datetime.now().isoformat()
    try:
        cursor.execute(
            """
            INSERT INTO registrations (event_id, student_name, roll_number, email, phone, registered_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """,
            (event_id, student_name, roll_number, email, phone, now_iso),
        )
        reg_id = cursor.lastrowid
        conn.commit()
    except sqlite3.IntegrityError:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Already registered",
        )

    conn.close()

    return RegistrationOut(
        id=reg_id,
        event_id=event_id,
        student_name=student_name,
        registered_at=now_iso,
    )


@app.get("/api/registrations", response_model=List[RegistrationDetail], summary="List all registrations")
def get_registrations(event_id: Optional[int] = Query(None, description="Optional filter by event ID")):
    """Returns list of student registrations, joined with event title."""
    conn = get_db_connection()
    cursor = conn.cursor()

    query = """
        SELECT 
            r.id, r.event_id, e.title AS event_title,
            r.student_name, r.roll_number, r.email, r.phone, r.registered_at
        FROM registrations r
        JOIN events e ON r.event_id = e.id
    """
    params = []
    if event_id is not None:
        query += " WHERE r.event_id = ?"
        params.append(event_id)

    query += " ORDER BY r.id DESC"

    cursor.execute(query, params)
    rows = cursor.fetchall()
    conn.close()

    return [
        RegistrationDetail(
            id=row["id"],
            event_id=row["event_id"],
            event_title=row["event_title"],
            student_name=row["student_name"],
            roll_number=row["roll_number"],
            email=row["email"],
            phone=row["phone"],
            registered_at=row["registered_at"],
        )
        for row in rows
    ]


@app.get("/api/stats", response_model=StatsOut, summary="Aggregated fest dashboard stats")
def get_stats():
    """Returns live stats computed directly from database on each call."""
    conn = get_db_connection()
    cursor = conn.cursor()

    # Total events
    cursor.execute("SELECT COUNT(*) FROM events")
    total_events = cursor.fetchone()[0]

    # Total registrations
    cursor.execute("SELECT COUNT(*) FROM registrations")
    total_registrations = cursor.fetchone()[0]

    # Total capacity
    cursor.execute("SELECT COALESCE(SUM(capacity), 0) FROM events")
    total_capacity = cursor.fetchone()[0]

    # Overall utilization percentage
    overall_utilization_pct = 0.0
    if total_capacity > 0:
        overall_utilization_pct = round((total_registrations / total_capacity) * 100, 1)

    # Upcoming events count (event_date >= today's date)
    today_iso = date.today().isoformat()
    cursor.execute("SELECT COUNT(*) FROM events WHERE event_date >= ?", (today_iso,))
    upcoming_events_count = cursor.fetchone()[0]

    # Events breakdown by category
    cursor.execute("SELECT category, COUNT(*) AS cat_count FROM events GROUP BY category ORDER BY category ASC")
    cat_rows = cursor.fetchall()
    events_by_category = {row["category"]: row["cat_count"] for row in cat_rows}

    conn.close()

    return StatsOut(
        total_events=total_events,
        total_registrations=total_registrations,
        total_capacity=total_capacity,
        overall_utilization_pct=overall_utilization_pct,
        upcoming_events_count=upcoming_events_count,
        events_by_category=events_by_category,
    )


# ==========================================
# STRETCH GOAL ENDPOINTS
# ==========================================

@app.post("/api/events", response_model=EventOut, status_code=status.HTTP_201_CREATED, summary="Stretch: Admin add event")
def create_event(payload: EventCreateIn):
    """Allows fest admin to add a new event with validation."""
    title = payload.title.strip()
    category = payload.category.strip()
    venue = payload.venue.strip()

    if not title:
        raise HTTPException(status_code=400, detail="Title cannot be empty")
    if not category:
        raise HTTPException(status_code=400, detail="Category cannot be empty")
    if not venue:
        raise HTTPException(status_code=400, detail="Venue cannot be empty")
    if payload.capacity <= 0:
        raise HTTPException(status_code=400, detail="Capacity must be greater than 0")

    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute(
        """
        INSERT INTO events (title, category, description, event_date, event_time, venue, capacity, fee, icon)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        (
            title,
            category,
            payload.description.strip() if payload.description else "",
            payload.event_date.strip(),
            payload.event_time.strip(),
            venue,
            payload.capacity,
            payload.fee,
            payload.icon.strip() if payload.icon else "🎪",
        ),
    )
    event_id = cursor.lastrowid
    conn.commit()
    conn.close()

    return EventOut(
        id=event_id,
        title=title,
        category=category,
        description=payload.description,
        event_date=payload.event_date,
        event_time=payload.event_time,
        venue=venue,
        capacity=payload.capacity,
        fee=payload.fee,
        icon=payload.icon or "🎪",
        available_seats=payload.capacity,
    )


@app.delete("/api/registrations/{registration_id}", summary="Stretch: Admin cancel registration")
def cancel_registration(registration_id: int):
    """Allows fest admin to cancel/remove a registration."""
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT id, event_id, student_name FROM registrations WHERE id = ?", (registration_id,))
    reg = cursor.fetchone()
    if not reg:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Registration with ID {registration_id} not found",
        )

    cursor.execute("DELETE FROM registrations WHERE id = ?", (registration_id,))
    conn.commit()
    conn.close()

    return {
        "message": "Registration cancelled successfully",
        "id": registration_id,
        "event_id": reg["event_id"],
        "student_name": reg["student_name"],
    }


# ==========================================
# STATIC FRONTEND SERVING (For Visual Reference)
# ==========================================

js_dir = FRONTEND_DIR / "js"
if js_dir.exists():
    app.mount("/js", StaticFiles(directory=str(js_dir)), name="js")


@app.get("/", include_in_schema=False)
def serve_index():
    """Serves the student event portal home page."""
    index_file = FRONTEND_DIR / "index.html"
    if not index_file.exists():
        raise HTTPException(status_code=404, detail="index.html not found")
    return FileResponse(index_file)


@app.get("/admin.html", include_in_schema=False)
def serve_admin():
    """Serves the coordinator admin dashboard page."""
    admin_file = FRONTEND_DIR / "admin.html"
    if not admin_file.exists():
        raise HTTPException(status_code=404, detail="admin.html not found")
    return FileResponse(admin_file)


@app.get("/mobile", include_in_schema=False)
@app.get("/android", include_in_schema=False)
def serve_mobile():
    """Serves the interactive Android emulator preview."""
    mobile_file = FRONTEND_DIR / "mobile.html"
    if not mobile_file.exists():
        raise HTTPException(status_code=404, detail="mobile.html not found")
    return FileResponse(mobile_file)

