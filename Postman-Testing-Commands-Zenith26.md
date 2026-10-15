# Zenith '26 Postman API Testing Guide & Checklist

**Companion to:** PM2-01 (How Software Works) and PM2-02 (Front-End, Back-End & Mobile)  
**Target App:** Zenith '26 College Fest Management System  
**Base URL:** `http://localhost:8000`

---

## 1. Postman Setup

1. Open the Postman Desktop application (or web client).
2. Click **Import** in the top left corner.
3. Drag and drop `zenith26-api.postman_collection.json` (located in `zenith26-fest-system/`).
4. Ensure the backend FastAPI server is running:
   ```bash
   uvicorn backend.main:app --reload
   ```
5. Confirm the collection variable `baseUrl` is set to `http://localhost:8000`.

---

## 2. All 18 Test Cases & API Contracts

### Category A: Events Discovery (`GET /api/events`)

#### Test 1: List all events
- **Method & URL:** `GET {{baseUrl}}/api/events`
- **Expected Status:** `200 OK`
- **Expected Body:** JSON Array containing 14 event objects. Each object contains `id`, `title`, `category`, `capacity`, `available_seats`, etc.
- **PM Concept:** Live seat calculation (`capacity - count(registrations)`).

#### Test 2: Filter by category (Technical)
- **Method & URL:** `GET {{baseUrl}}/api/events?category=Technical`
- **Expected Status:** `200 OK`
- **Expected Body:** JSON Array where every event has `"category": "Technical"`.

#### Test 3: Filter by category (Cultural)
- **Method & URL:** `GET {{baseUrl}}/api/events?category=Cultural`
- **Expected Status:** `200 OK`
- **Expected Body:** JSON Array of Cultural events (Battle of Bands, Dance Fiesta, etc.).

#### Test 4: Search keyword match
- **Method & URL:** `GET {{baseUrl}}/api/events?search=code`
- **Expected Status:** `200 OK`
- **Expected Body:** List containing `"Code Sprint 2.0"`.

#### Test 5: Search keyword with no match
- **Method & URL:** `GET {{baseUrl}}/api/events?search=NonExistentKeywordXYZ`
- **Expected Status:** `200 OK`
- **Expected Body:** Empty array `[]`.

---

### Category B: Single Event Details (`GET /api/events/{id}`)

#### Test 6: Valid event ID
- **Method & URL:** `GET {{baseUrl}}/api/events/1`
- **Expected Status:** `200 OK`
- **Expected Body:** Object with `"id": 1`, `"title": "Code Sprint 2.0"`.

#### Test 7: Non-existent event ID (404)
- **Method & URL:** `GET {{baseUrl}}/api/events/9999`
- **Expected Status:** `404 Not Found`
- **Expected Body:** `{"detail": "Event with ID 9999 not found"}`
- **PM Concept:** Clean error handling prevents client-side ambiguity.

---

### Category C: Student Registration (`POST /api/events/{id}/register`)

#### Test 8: Successful registration (201)
- **Method & URL:** `POST {{baseUrl}}/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "student_name": "Rohan Sharma",
    "roll_number": "2026CS042",
    "email": "rohan.sharma@zenith.edu",
    "phone": "+91 9876543210"
  }
  ```
- **Expected Status:** `201 Created`
- **Expected Body:**
  ```json
  {
    "id": 1,
    "event_id": 1,
    "student_name": "Rohan Sharma",
    "registered_at": "<ISO-timestamp>"
  }
  ```

#### Test 9: Duplicate registration prevention (409)
- **Method & URL:** `POST {{baseUrl}}/api/events/1/register`
- **Body:** *(Re-send the exact same body from Test 8)*
- **Expected Status:** `409 Conflict`
- **Expected Body:** `{"detail": "Already registered"}`
- **PM Concept:** Database uniqueness guarantees consistency across concurrent requests.

#### Test 10: Missing student name (400)
- **Method & URL:** `POST {{baseUrl}}/api/events/1/register`
- **Body:**
  ```json
  {
    "student_name": "",
    "roll_number": "2026CS042",
    "email": "student2@zenith.edu"
  }
  ```
- **Expected Status:** `400 Bad Request`
- **Expected Body:** `{"detail": "Student name is required"}`

#### Test 11: Missing roll number (400)
- **Method & URL:** `POST {{baseUrl}}/api/events/1/register`
- **Body:**
  ```json
  {
    "student_name": "Pooja Patel",
    "roll_number": "",
    "email": "pooja@zenith.edu"
  }
  ```
- **Expected Status:** `400 Bad Request`
- **Expected Body:** `{"detail": "Roll number is required"}`

#### Test 12: Invalid email address (400)
- **Method & URL:** `POST {{baseUrl}}/api/events/1/register`
- **Body:**
  ```json
  {
    "student_name": "Pooja Patel",
    "roll_number": "2026CS089",
    "email": "invalid-email-address"
  }
  ```
- **Expected Status:** `400 Bad Request`
- **Expected Body:** `{"detail": "Invalid email address format"}`

#### Test 13: Registering for a non-existent event (404)
- **Method & URL:** `POST {{baseUrl}}/api/events/9999/register`
- **Body:**
  ```json
  {
    "student_name": "Pooja Patel",
    "roll_number": "2026CS089",
    "email": "pooja@zenith.edu"
  }
  ```
- **Expected Status:** `404 Not Found`
- **Expected Body:** `{"detail": "Event with ID 9999 not found"}`

---

### Category D: Registration Roster (`GET /api/registrations`)

#### Test 14: Get all registrations
- **Method & URL:** `GET {{baseUrl}}/api/registrations`
- **Expected Status:** `200 OK`
- **Expected Body:** List of registrations including `"event_title"` for each.

#### Test 15: Filter registrations by event ID
- **Method & URL:** `GET {{baseUrl}}/api/registrations?event_id=1`
- **Expected Status:** `200 OK`
- **Expected Body:** Array containing only registrations where `"event_id": 1`.

---

### Category E: Live Fest Statistics (`GET /api/stats`)

#### Test 16: Live aggregated metrics
- **Method & URL:** `GET {{baseUrl}}/api/stats`
- **Expected Status:** `200 OK`
- **Expected Body:**
  ```json
  {
    "total_events": 14,
    "total_registrations": 1,
    "total_capacity": 1437,
    "overall_utilization_pct": 0.1,
    "upcoming_events_count": 14,
    "events_by_category": {
      "Cultural": 5,
      "Gaming": 1,
      "Sports": 2,
      "Technical": 4,
      "Workshop": 2
    }
  }
  ```
- **PM Concept:** Single source of truth. Metrics are computed dynamically on every call, never stored as stale cache.

---

### Category F: Stretch Goals

#### Test 17: Admin creates new event
- **Method & URL:** `POST {{baseUrl}}/api/events`
- **Body:**
  ```json
  {
    "title": "Autonomous Drone Challenge",
    "category": "Technical",
    "description": "Obstacle navigation test for quadcopters.",
    "event_date": "2026-11-15",
    "event_time": "04:00 PM",
    "venue": "Outdoor Quad",
    "capacity": 25,
    "fee": 50,
    "icon": "🛸"
  }
  ```
- **Expected Status:** `201 Created`

#### Test 18: Admin cancels registration
- **Method & URL:** `DELETE {{baseUrl}}/api/registrations/1`
- **Expected Status:** `200 OK`
- **Expected Body:** `{"message": "Registration cancelled successfully", "id": 1, ...}`
- **Verification:** Re-running `GET /api/events/1` immediately shows `available_seats` increased back by 1!
