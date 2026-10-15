package com.zenith26.fest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.EventCreateIn
import com.zenith26.fest.data.model.RegistrationDetail
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.model.StatsOut
import com.zenith26.fest.data.repository.FestRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AdminTab {
    EVENTS_TABLE,
    REGISTRATIONS_ROSTER
}

class FestViewModel(
    private val repository: FestRepository = FestRepository()
) : ViewModel() {

    // --- Student Portal State ---
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents: StateFlow<Boolean> = _isLoadingEvents.asStateFlow()

    private val _eventsError = MutableStateFlow<String?>(null)
    val eventsError: StateFlow<String?> = _eventsError.asStateFlow()

    private val _currentCategory = MutableStateFlow("All")
    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- Modal / Registration State ---
    private val _activeModalEvent = MutableStateFlow<Event?>(null)
    val activeModalEvent: StateFlow<Event?> = _activeModalEvent.asStateFlow()

    private val _isRegistering = MutableStateFlow(false)
    val isRegistering: StateFlow<Boolean> = _isRegistering.asStateFlow()

    private val _registrationSuccessMsg = MutableStateFlow<String?>(null)
    val registrationSuccessMsg: StateFlow<String?> = _registrationSuccessMsg.asStateFlow()

    private val _registrationErrorMsg = MutableStateFlow<String?>(null)
    val registrationErrorMsg: StateFlow<String?> = _registrationErrorMsg.asStateFlow()

    // --- Admin Dashboard State ---
    private val _stats = MutableStateFlow<StatsOut?>(null)
    val stats: StateFlow<StatsOut?> = _stats.asStateFlow()

    private val _isLoadingStats = MutableStateFlow(false)
    val isLoadingStats: StateFlow<Boolean> = _isLoadingStats.asStateFlow()

    private val _statsError = MutableStateFlow<String?>(null)
    val statsError: StateFlow<String?> = _statsError.asStateFlow()

    private val _registrations = MutableStateFlow<List<RegistrationDetail>>(emptyList())
    val registrations: StateFlow<List<RegistrationDetail>> = _registrations.asStateFlow()

    private val _isLoadingRegistrations = MutableStateFlow(false)
    val isLoadingRegistrations: StateFlow<Boolean> = _isLoadingRegistrations.asStateFlow()

    private val _selectedRosterEventId = MutableStateFlow<Int?>(null)
    val selectedRosterEventId: StateFlow<Int?> = _selectedRosterEventId.asStateFlow()

    private val _adminTab = MutableStateFlow(AdminTab.EVENTS_TABLE)
    val adminTab: StateFlow<AdminTab> = _adminTab.asStateFlow()

    private val _showAddEventDialog = MutableStateFlow(false)
    val showAddEventDialog: StateFlow<Boolean> = _showAddEventDialog.asStateFlow()

    private val _isCreatingEvent = MutableStateFlow(false)
    val isCreatingEvent: StateFlow<Boolean> = _isCreatingEvent.asStateFlow()

    private val _addEventError = MutableStateFlow<String?>(null)
    val addEventError: StateFlow<String?> = _addEventError.asStateFlow()

    private var searchDebounceJob: Job? = null

    init {
        fetchEvents()
        fetchStats()
    }

    /**
     * Fetch events from the backend using active category and search query.
     */
    fun fetchEvents() {
        viewModelScope.launch {
            _isLoadingEvents.value = true
            _eventsError.value = null

            val result = repository.getEvents(_currentCategory.value, _searchQuery.value)
            result.onSuccess { list ->
                _events.value = list
                _isLoadingEvents.value = false
            }.onFailure { err ->
                _eventsError.value = err.message ?: "Failed to connect to backend server. Make sure FastAPI is running on http://10.0.2.2:8000."
                _isLoadingEvents.value = false
            }
        }
    }

    fun selectCategory(category: String) {
        if (_currentCategory.value == category) return
        _currentCategory.value = category
        fetchEvents()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(300) // Debounce typing
            fetchEvents()
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        fetchEvents()
    }

    fun resetAllFilters() {
        _currentCategory.value = "All"
        _searchQuery.value = ""
        fetchEvents()
    }

    // --- Modal Registration Methods ---

    fun openEventModal(event: Event) {
        _activeModalEvent.value = event
        _registrationErrorMsg.value = null
        _registrationSuccessMsg.value = null
        _isRegistering.value = false
    }

    fun closeEventModal() {
        _activeModalEvent.value = null
        _registrationErrorMsg.value = null
        _registrationSuccessMsg.value = null
        _isRegistering.value = false
    }

    fun submitRegistration(name: String, roll: String, email: String, phone: String?) {
        val event = _activeModalEvent.value ?: return

        // 1. Client-side checks
        val trimmedName = name.trim()
        val trimmedRoll = roll.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isBlank()) {
            _registrationErrorMsg.value = "Please enter your full name."
            return
        }
        if (trimmedRoll.isBlank()) {
            _registrationErrorMsg.value = "Roll number is required."
            return
        }
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@") || trimmedEmail.length < 4) {
            _registrationErrorMsg.value = "Please provide a valid email containing '@'."
            return
        }

        viewModelScope.launch {
            _isRegistering.value = true
            _registrationErrorMsg.value = null
            _registrationSuccessMsg.value = null

            val payload = RegistrationIn(
                studentName = trimmedName,
                rollNumber = trimmedRoll,
                email = trimmedEmail,
                phone = phone?.trim()?.ifBlank { null }
            )

            val result = repository.registerStudent(event.id, payload)
            result.onSuccess { regOut ->
                _isRegistering.value = false
                _registrationSuccessMsg.value = "You're registered for ${event.title}! Registration ID: #${regOut.id}"

                // Immediately re-fetch events to decrement seat count on screen
                fetchEvents()
                fetchStats()
            }.onFailure { err ->
                _isRegistering.value = false
                _registrationErrorMsg.value = err.message ?: "Registration failed."
            }
        }
    }

    // --- Admin Dashboard Methods ---

    fun fetchStats() {
        viewModelScope.launch {
            _isLoadingStats.value = true
            _statsError.value = null

            val result = repository.getStats()
            result.onSuccess { s ->
                _stats.value = s
                _isLoadingStats.value = false
            }.onFailure { err ->
                _statsError.value = err.message ?: "Unable to sync live data from backend."
                _isLoadingStats.value = false
            }
        }
    }

    fun fetchRegistrations(eventId: Int? = _selectedRosterEventId.value) {
        viewModelScope.launch {
            _isLoadingRegistrations.value = true
            val result = repository.getRegistrations(eventId)
            result.onSuccess { list ->
                _registrations.value = list
                _isLoadingRegistrations.value = false
            }.onFailure {
                _isLoadingRegistrations.value = false
            }
        }
    }

    fun setRosterEventFilter(eventId: Int?) {
        _selectedRosterEventId.value = eventId
        fetchRegistrations(eventId)
    }

    fun setAdminTab(tab: AdminTab) {
        _adminTab.value = tab
        if (tab == AdminTab.REGISTRATIONS_ROSTER) {
            fetchRegistrations()
        }
    }

    fun refreshDashboard() {
        fetchStats()
        fetchEvents()
        if (_adminTab.value == AdminTab.REGISTRATIONS_ROSTER) {
            fetchRegistrations()
        }
    }

    fun setShowAddEventDialog(show: Boolean) {
        _showAddEventDialog.value = show
        _addEventError.value = null
    }

    fun submitCreateEvent(eventIn: EventCreateIn) {
        viewModelScope.launch {
            _isCreatingEvent.value = true
            _addEventError.value = null

            val result = repository.createEvent(eventIn)
            result.onSuccess {
                _isCreatingEvent.value = false
                _showAddEventDialog.value = false
                fetchEvents()
                fetchStats()
            }.onFailure { err ->
                _isCreatingEvent.value = false
                _addEventError.value = err.message ?: "Failed to create event."
            }
        }
    }

    fun cancelRegistration(registrationId: Int) {
        viewModelScope.launch {
            val result = repository.cancelRegistration(registrationId)
            result.onSuccess {
                fetchRegistrations()
                fetchEvents()
                fetchStats()
            }
        }
    }
}
