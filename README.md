# Zenith '26 College Fest Management System (Native Android + FastAPI)

A full-stack, native Android + Python/FastAPI college fest management platform built for **PM2-01 (How Software Works)** and **PM2-02 (Front-End, Back-End & Mobile)**.

---

## 1. Project Overview

**Zenith '26** is a college fest management system comprising:
1. **Student Event Portal (Native Android):** Browse 14 campus fest events, live seat availability tracking, search, category filter pills, event details, and registration forms with validation.
2. **Fest Coordinator Admin Dashboard (Native Android):** Live fest metrics (Total Events, Total Registrations, Capacity Utilization %, Upcoming Events), category distribution breakdown, event capacity utilization table with sorted demand & >90% warning badges, and raw student registration roster.
3. **Backend REST API (FastAPI):** Python REST API enforcing server-side validation ("never trust the client"), status code discipline (200, 201, 400, 404, 409), and database consistency.
4. **Data Layer (SQLite):** `fest.db` containing `events` and `registrations` tables, initialized and seeded directly from `data/events_seed.xlsx`.

---

## 2. Tech Stack

- **Mobile Frontend:** Kotlin, Jetpack Compose, Material 3, ViewModel, Coroutines, StateFlow
- **Network Client:** Retrofit 2, OkHttp 3, Gson
- **Backend API:** Python 3.10+, FastAPI, Uvicorn, Pydantic v2
- **Database:** SQLite3 (`fest.db`) with Foreign Keys & Unique Constraints
- **Seed Data:** OpenPyXL reading `data/events_seed.xlsx`
- **API Testing:** Postman Desktop App (`zenith26-api.postman_collection.json`)

---

## 3. Project Directory Structure

```text
zenith26-fest-system/
├── backend/
│   ├── database.py       # SQLite connection and schema creation
│   ├── main.py           # FastAPI REST endpoints & lifecycle hooks
│   ├── models.py         # Pydantic schemas for API contracts
│   └── seed_data.py      # Spreadsheet-to-SQLite seed script
├── android/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/zenith26/fest/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/ (ApiClient.kt, ZenithApiService.kt)
│   │   │   │   │   ├── model/ (Event.kt, Registration.kt, Stats.kt)
│   │   │   │   │   └── repository/ (FestRepository.kt)
│   │   │   │   └── ui/
│   │   │   │       ├── components/ (Navbar, Hero, EventCard, etc.)
│   │   │   │       ├── screens/ (StudentPortal, AdminDashboard, MainScreen)
│   │   │   │       ├── theme/ (Color, Theme, Type)
│   │   │   │       └── viewmodel/ (FestViewModel.kt)
│   │   │   ├── res/ (drawable, values, xml)
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
│   ├── build.gradle
│   ├── settings.gradle
│   └── gradle.properties
├── data/
│   └── events_seed.xlsx  # 14 Seed events spreadsheet
├── requirements.txt
├── README.md
├── zenith26-api.postman_collection.json
└── Postman-Testing-Commands-Zenith26.md
```

---

## 4. How to Run the Backend

### Step 1: Activate Virtual Environment
On Windows (PowerShell):
```powershell
cd zenith26-fest-system
.\.venv\Scripts\Activate.ps1
```

If the virtual environment is not yet created:
```powershell
python -m venv .venv
.\.venv\Scripts\pip install -r requirements.txt
```

### Step 2: Seed the Database
Run the seed script to load the 14 events from `data/events_seed.xlsx`:
```powershell
python backend/seed_data.py
```

### Step 3: Start the FastAPI Server
```powershell
uvicorn backend.main:app --reload --host 0.0.0.0 --port 8000
```
- Interactive Swagger API docs: **http://localhost:8000/docs**
- Alternative ReDoc API docs: **http://localhost:8000/redoc**
- Web UI visual reference: **http://localhost:8000/** and **http://localhost:8000/admin.html**

---

## 5. How to Run the Android App

### Opening in Android Studio
1. Launch **Android Studio**.
2. Select **Open** and choose the `zenith26-fest-system/android` directory.
3. Allow Gradle to sync dependencies.
4. Select an Android Virtual Device (AVD / Emulator) or connected physical device.
5. Click **Run (Shift + F10)** to install and launch `Zenith '26`.

### API Base URL Configuration
The API base URL is configured in `com.zenith26.fest.data.api.ApiClient.kt`:
- **Android Emulator:** Uses `http://10.0.2.2:8000/` by default (special alias to access the host computer's `localhost:8000`).
- **Physical Android Device:** Set to your computer's local Wi-Fi LAN IP (e.g., `http://192.168.1.15:8000/`). Ensure both your computer and phone are connected to the same Wi-Fi network and that firewall allows port 8000.
- Runtime modification is available via `ApiClient.setBaseUrl("http://<YOUR_IP>:8000/")`.

---

## 6. API Endpoints Contract

| Method | Endpoint | Query / Body | Description & Status Codes |
|---|---|---|---|
| `GET` | `/api/events` | `?category=...&search=...` | Returns list of events with live computed `available_seats`. (200 OK) |
| `GET` | `/api/events/{id}` | Path param `id` | Detail of an event. (200 OK, 404 Not Found) |
| `POST` | `/api/events/{id}/register` | JSON `RegistrationIn` | Registers student. Enforces capacity & duplicate email check. (201 Created, 400 Bad Request, 404 Not Found, 409 Conflict) |
| `GET` | `/api/registrations` | `?event_id=...` | Roster of registrations joined with event title. (200 OK) |
| `GET` | `/api/stats` | None | Live metrics: total events, total registrations, capacity, utilization %. (200 OK) |
| `POST` | `/api/events` | JSON `EventCreateIn` | Admin stretch goal: Add event. (201 Created, 400 Bad Request) |
| `DELETE` | `/api/registrations/{id}` | Path param `id` | Admin stretch goal: Cancel registration. (200 OK, 404 Not Found) |

---

## 7. Testing with Postman

1. Open the Postman Desktop App.
2. Click **Import** and select `zenith26-api.postman_collection.json`.
3. Set the `baseUrl` variable to `http://localhost:8000`.
4. Run requests in folder order:
   - `GET /api/events`
   - `GET /api/events/1`
   - `POST /api/events/1/register` (Success - 201)
   - `POST /api/events/1/register` (Duplicate email - 409)
   - `POST /api/events/1/register` (Missing fields / Invalid email - 400)
   - `GET /api/stats`
5. Refer to `Postman-Testing-Commands-Zenith26.md` for full test details and expected JSON responses.
