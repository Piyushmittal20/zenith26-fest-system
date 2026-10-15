import sys
from pathlib import Path
from fastapi.testclient import TestClient

sys.path.insert(0, str(Path(__file__).resolve().parent))

from backend.main import app

client = TestClient(app)

def run_tests():
    print("Testing GET /api/events...")
    r = client.get("/api/events")
    assert r.status_code == 200, f"Expected 200, got {r.status_code}: {r.text}"
    events = r.json()
    assert len(events) == 14, f"Expected 14 events, got {len(events)}"
    print(f" PASS: {len(events)} events loaded.")

    print("\nTesting GET /api/events?category=Technical...")
    r = client.get("/api/events?category=Technical")
    assert r.status_code == 200
    tech_events = r.json()
    assert len(tech_events) == 4, f"Expected 4 tech events, got {len(tech_events)}"
    print(f" PASS: {len(tech_events)} Technical events found.")

    print("\nTesting GET /api/events?search=code...")
    r = client.get("/api/events?search=code")
    assert r.status_code == 200
    search_events = r.json()
    assert len(search_events) >= 1
    print(f" PASS: Found {len(search_events)} events matching 'code'.")

    print("\nTesting GET /api/events/1...")
    r = client.get("/api/events/1")
    assert r.status_code == 200
    ev1 = r.json()
    assert ev1["id"] == 1 and ev1["title"] == "Code Sprint 2.0"
    print(f" PASS: Event 1 is '{ev1['title']}'.")

    print("\nTesting GET /api/events/999 (404 test)...")
    r = client.get("/api/events/999")
    assert r.status_code == 404, f"Expected 404, got {r.status_code}"
    print(f" PASS: 404 handled: {r.json()['detail']}")

    print("\nTesting POST /api/events/1/register (Valid registration)...")
    payload = {
        "student_name": "Test Student",
        "roll_number": "2026TEST001",
        "email": "teststudent@campus.edu",
        "phone": "+919876543210"
    }
    r = client.post("/api/events/1/register", json=payload)
    assert r.status_code in (201, 409), f"Unexpected status: {r.status_code}"
    print(f" PASS: Registration result: {r.status_code} - {r.json()}")

    print("\nTesting POST /api/events/1/register duplicate (409 test)...")
    r = client.post("/api/events/1/register", json=payload)
    assert r.status_code == 409, f"Expected 409 for duplicate, got {r.status_code}"
    print(f" PASS: 409 duplicate rejected: {r.json()['detail']}")

    print("\nTesting POST /api/events/1/register invalid email (400 test)...")
    bad_payload = {
        "student_name": "Test Student",
        "roll_number": "2026TEST001",
        "email": "bademailformat",
        "phone": ""
    }
    r = client.post("/api/events/1/register", json=bad_payload)
    assert r.status_code == 400, f"Expected 400, got {r.status_code}"
    print(f" PASS: 400 invalid email caught: {r.json()['detail']}")

    print("\nTesting GET /api/registrations...")
    r = client.get("/api/registrations")
    assert r.status_code == 200
    regs = r.json()
    assert len(regs) >= 1
    print(f" PASS: {len(regs)} registrations found.")

    print("\nTesting GET /api/stats...")
    r = client.get("/api/stats")
    assert r.status_code == 200
    stats = r.json()
    print(f" PASS: Stats: Total Events={stats['total_events']}, Registrations={stats['total_registrations']}, Capacity={stats['total_capacity']}, Utilization={stats['overall_utilization_pct']}%")

    print("\nAll Backend Tests Passed Successfully!")

if __name__ == "__main__":
    run_tests()
