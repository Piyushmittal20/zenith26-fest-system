from typing import Dict, Optional
from pydantic import BaseModel, Field


class EventOut(BaseModel):
    """Schema for event representation in lists."""
    id: int = Field(..., description="Unique event identifier")
    title: str = Field(..., description="Event title")
    category: str = Field(..., description="Category (Technical, Cultural, Sports, Gaming, Workshop)")
    description: Optional[str] = Field(None, description="Detailed description of the event")
    event_date: str = Field(..., description="Event date in YYYY-MM-DD format")
    event_time: str = Field(..., description="Event start time (e.g. 10:00 AM)")
    venue: str = Field(..., description="Campus venue location")
    capacity: int = Field(..., description="Total attendee capacity")
    fee: int = Field(0, description="Entry fee in INR (0 means Free)")
    icon: Optional[str] = Field(None, description="Event emoji icon")
    available_seats: int = Field(..., description="Remaining capacity (capacity - total registrations)")


class EventDetail(EventOut):
    """Schema for single event detail view (same shape as EventOut)."""
    pass


class RegistrationIn(BaseModel):
    """Input payload submitted when registering for an event."""
    student_name: str = Field(..., description="Full name of student")
    roll_number: str = Field(..., description="College roll number / student ID")
    email: str = Field(..., description="Student email address")
    phone: Optional[str] = Field(None, description="Optional contact phone number")


class RegistrationOut(BaseModel):
    """Confirmation payload returned on successful registration."""
    id: int = Field(..., description="Unique registration ID")
    event_id: int = Field(..., description="Associated event ID")
    student_name: str = Field(..., description="Student full name")
    registered_at: str = Field(..., description="ISO timestamp when registration was processed")


class RegistrationDetail(BaseModel):
    """Detailed registration information for admin reporting."""
    id: int
    event_id: int
    event_title: str
    student_name: str
    roll_number: str
    email: str
    phone: Optional[str] = None
    registered_at: str


class StatsOut(BaseModel):
    """Overall fest performance metrics computed live."""
    total_events: int = Field(..., description="Total count of active events")
    total_registrations: int = Field(..., description="Total registrations across all events")
    total_capacity: int = Field(..., description="Sum of capacities of all events")
    overall_utilization_pct: float = Field(..., description="Overall capacity utilization percentage (0-100)")
    upcoming_events_count: int = Field(..., description="Number of events happening today or later")
    events_by_category: Dict[str, int] = Field(..., description="Breakdown of event counts per category")


class EventCreateIn(BaseModel):
    """Payload for creating a new event (admin stretch goal)."""
    title: str = Field(..., min_length=1, description="Event title")
    category: str = Field(..., min_length=1, description="Event category")
    description: Optional[str] = Field(None, description="Detailed description")
    event_date: str = Field(..., description="Date formatted as YYYY-MM-DD")
    event_time: str = Field(..., description="Time string (e.g. 10:00 AM)")
    venue: str = Field(..., min_length=1, description="Venue location")
    capacity: int = Field(..., gt=0, description="Maximum capacity (must be greater than 0)")
    fee: int = Field(0, ge=0, description="Registration fee in INR")
    icon: Optional[str] = Field("🎪", description="Emoji icon")
