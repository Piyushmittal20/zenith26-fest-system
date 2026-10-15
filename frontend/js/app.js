/**
 * Zenith '26 College Fest Management System
 * Student Portal JavaScript (app.js)
 */

document.addEventListener('DOMContentLoaded', () => {
  // State
  let currentCategory = 'All';
  let searchQuery = '';
  let searchDebounceTimeout = null;
  let activeEventData = null;

  // DOM Elements
  const eventsGrid = document.getElementById('events-grid');
  const searchInput = document.getElementById('search-input');
  const clearSearchBtn = document.getElementById('clear-search-btn');
  const categoryFilters = document.getElementById('category-filters');
  const catButtons = document.querySelectorAll('.cat-filter-btn');

  const statusContainer = document.getElementById('status-container');
  const loadingState = document.getElementById('loading-state');
  const errorState = document.getElementById('error-state');
  const errorMessage = document.getElementById('error-message');
  const retryBtn = document.getElementById('retry-btn');
  const emptyState = document.getElementById('empty-state');
  const resetFilterBtn = document.getElementById('reset-filter-btn');

  // Modal Elements
  const modal = document.getElementById('event-modal');
  const modalBackdrop = document.getElementById('modal-backdrop');
  const closeModalBtn = document.getElementById('close-modal-btn');
  const modalIcon = document.getElementById('modal-event-icon');
  const modalCategory = document.getElementById('modal-event-category');
  const modalFee = document.getElementById('modal-event-fee');
  const modalTitle = document.getElementById('modal-event-title');
  const modalDescription = document.getElementById('modal-event-description');
  const modalDatetime = document.getElementById('modal-event-datetime');
  const modalVenue = document.getElementById('modal-event-venue');
  const modalSeats = document.getElementById('modal-event-seats');
  const modalStatus = document.getElementById('modal-event-status');

  // Registration Form Elements
  const regForm = document.getElementById('registration-form');
  const regEventIdInput = document.getElementById('reg-event-id');
  const regNameInput = document.getElementById('reg-name');
  const regRollInput = document.getElementById('reg-roll');
  const regEmailInput = document.getElementById('reg-email');
  const regPhoneInput = document.getElementById('reg-phone');
  const submitRegBtn = document.getElementById('submit-reg-btn');
  const submitBtnSpinner = document.getElementById('submit-btn-spinner');
  const submitBtnText = document.getElementById('submit-btn-text');

  const errName = document.getElementById('err-name');
  const errRoll = document.getElementById('err-roll');
  const errEmail = document.getElementById('err-email');
  const modalAlertError = document.getElementById('modal-alert-error');
  const modalAlertErrorMsg = document.getElementById('modal-alert-error-msg');
  const modalAlertSuccess = document.getElementById('modal-alert-success');
  const modalAlertSuccessMsg = document.getElementById('modal-alert-success-msg');

  // Category Badge Colors Helper
  const getCategoryStyles = (category) => {
    switch ((category || '').toLowerCase()) {
      case 'technical':
        return 'bg-blue-900/60 text-blue-300 border-blue-700/50';
      case 'cultural':
        return 'bg-pink-900/60 text-pink-300 border-pink-700/50';
      case 'sports':
        return 'bg-emerald-900/60 text-emerald-300 border-emerald-700/50';
      case 'gaming':
        return 'bg-purple-900/60 text-purple-300 border-purple-700/50';
      case 'workshop':
        return 'bg-amber-900/60 text-amber-300 border-amber-700/50';
      default:
        return 'bg-slate-800 text-slate-300 border-slate-700';
    }
  };

  /**
   * Fetch events from backend API using real query parameters.
   */
  async function fetchEvents() {
    showLoading();

    try {
      const params = new URLSearchParams();
      if (currentCategory && currentCategory !== 'All') {
        params.append('category', currentCategory);
      }
      if (searchQuery && searchQuery.trim() !== '') {
        params.append('search', searchQuery.trim());
      }

      const url = `/api/events${params.toString() ? '?' + params.toString() : ''}`;
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`HTTP Error: ${response.status} ${response.statusText}`);
      }

      const events = await response.json();
      renderEvents(events);
    } catch (err) {
      console.error('Error fetching events:', err);
      showError(err.message || 'Unable to connect to backend server. Make sure FastAPI is running on http://localhost:8000.');
    }
  }

  /**
   * Render event cards into grid.
   */
  function renderEvents(events) {
    eventsGrid.innerHTML = '';

    if (!events || events.length === 0) {
      showEmpty();
      return;
    }

    hideStatus();
    eventsGrid.classList.remove('hidden');

    events.forEach((event, index) => {
      const isFull = event.available_seats <= 0;
      const catStyle = getCategoryStyles(event.category);
      const feeLabel = event.fee > 0 ? `₹${event.fee}` : 'Free';

      const card = document.createElement('div');
      card.className = 'glass-card rounded-2xl p-6 flex flex-col justify-between transition-all duration-300 hover:border-purple-500/50 hover:shadow-xl hover:shadow-purple-950/20 hover:-translate-y-1 animate-fade-in';
      card.style.animationDelay = `${index * 40}ms`;

      card.innerHTML = `
        <div>
          <!-- Header: Category & Status / Fee -->
          <div class="flex items-center justify-between gap-2 mb-4">
            <span class="px-2.5 py-1 rounded-full text-xs font-semibold border ${catStyle}">
              ${event.category}
            </span>
            <div class="flex items-center gap-1.5">
              ${
                isFull
                  ? '<span class="px-2.5 py-0.5 rounded-full text-xs font-bold bg-rose-950/80 text-rose-300 border border-rose-700/80 tracking-wide uppercase animate-pulse">FULL</span>'
                  : `<span class="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-950/60 text-emerald-300 border border-emerald-800/60">${feeLabel}</span>`
              }
            </div>
          </div>

          <!-- Title & Icon -->
          <div class="flex items-start gap-3.5 mb-3">
            <div class="text-3xl p-2 bg-slate-900 border border-slate-800 rounded-xl flex-shrink-0 flex items-center justify-center">
              ${event.icon || '⚡'}
            </div>
            <div class="min-w-0">
              <h3 class="text-lg font-bold text-white tracking-tight leading-snug group-hover:text-purple-300 transition-colors">
                ${escapeHtml(event.title)}
              </h3>
              <p class="text-xs text-slate-400 mt-1 line-clamp-2">
                ${escapeHtml(event.description || 'Join us for this exciting campus fest event!')}
              </p>
            </div>
          </div>

          <!-- Schedule & Venue Details -->
          <div class="mt-4 pt-3.5 border-t border-slate-800/80 space-y-2 text-xs text-slate-300">
            <div class="flex items-center gap-2">
              <span class="text-slate-400">🕒</span>
              <span>${escapeHtml(event.event_date)} · ${escapeHtml(event.event_time)}</span>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-slate-400">📍</span>
              <span class="truncate">${escapeHtml(event.venue)}</span>
            </div>
          </div>
        </div>

        <!-- Card Footer: Seats & Action -->
        <div class="mt-5 pt-4 border-t border-slate-800/80 flex items-center justify-between gap-3">
          <div class="text-xs">
            ${
              isFull
                ? '<span class="text-rose-400 font-semibold">0 seats available</span>'
                : `<span class="text-purple-300 font-semibold">${event.available_seats}</span> <span class="text-slate-400">/ ${event.capacity} seats left</span>`
            }
          </div>

          <button 
            data-id="${event.id}"
            class="view-details-btn px-4 py-2 text-xs font-bold rounded-xl transition-all duration-200 ${
              isFull
                ? 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                : 'bg-purple-600 hover:bg-purple-500 text-white shadow-lg shadow-purple-600/30 active:scale-95'
            }"
          >
            ${isFull ? 'View Info' : 'View Details'}
          </button>
        </div>
      `;

      eventsGrid.appendChild(card);
    });

    // Attach click listeners to "View Details" buttons
    document.querySelectorAll('.view-details-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const eventId = btn.getAttribute('data-id');
        openEventModal(eventId);
      });
    });
  }

  /**
   * Open detail & registration modal for an event.
   */
  async function openEventModal(eventId) {
    resetModalForm();
    modal.classList.remove('hidden');
    document.body.classList.add('overflow-hidden');

    try {
      const response = await fetch(`/api/events/${eventId}`);
      if (!response.ok) {
        throw new Error('Event could not be loaded');
      }
      activeEventData = await response.json();
      populateModal(activeEventData);
    } catch (err) {
      console.error(err);
      modalAlertError.classList.remove('hidden');
      modalAlertErrorMsg.textContent = 'Could not load event information.';
    }
  }

  function populateModal(event) {
    modalIcon.textContent = event.icon || '⚡';
    modalCategory.textContent = event.category;
    modalCategory.className = `px-2.5 py-0.5 rounded-full text-xs font-semibold border ${getCategoryStyles(event.category)}`;

    const feeLabel = event.fee > 0 ? `₹${event.fee}` : 'Free';
    modalFee.textContent = feeLabel;
    modalTitle.textContent = event.title;
    modalDescription.textContent = event.description || 'No description provided.';
    modalDatetime.textContent = `${event.event_date} · ${event.event_time}`;
    modalVenue.textContent = event.venue;

    const isFull = event.available_seats <= 0;
    regEventIdInput.value = event.id;

    if (isFull) {
      modalSeats.textContent = `0 / ${event.capacity} seats remaining`;
      modalSeats.className = 'font-semibold text-rose-400';
      modalStatus.textContent = 'Registration Closed (FULL)';
      modalStatus.className = 'font-semibold text-rose-400';

      submitRegBtn.disabled = true;
      submitRegBtn.classList.add('opacity-50', 'cursor-not-allowed');
      submitBtnText.textContent = 'Event is Full';
    } else {
      modalSeats.textContent = `${event.available_seats} / ${event.capacity} available`;
      modalSeats.className = 'font-semibold text-purple-400';
      modalStatus.textContent = 'Registration Open';
      modalStatus.className = 'font-semibold text-emerald-400';

      submitRegBtn.disabled = false;
      submitRegBtn.classList.remove('opacity-50', 'cursor-not-allowed');
      submitBtnText.textContent = 'Confirm Registration';
    }
  }

  function closeModal() {
    modal.classList.add('hidden');
    document.body.classList.remove('overflow-hidden');
    activeEventData = null;
    resetModalForm();
  }

  function resetModalForm() {
    regForm.reset();
    errName.classList.add('hidden');
    errRoll.classList.add('hidden');
    errEmail.classList.add('hidden');
    modalAlertError.classList.add('hidden');
    modalAlertSuccess.classList.add('hidden');
    submitRegBtn.disabled = false;
    submitRegBtn.classList.remove('opacity-50', 'cursor-not-allowed');
    submitBtnSpinner.classList.add('hidden');
    submitBtnText.textContent = 'Confirm Registration';
  }

  /**
   * Client-side validation helper.
   */
  function validateForm() {
    let isValid = true;

    // Name
    const nameVal = regNameInput.value.trim();
    if (!nameVal) {
      errName.classList.remove('hidden');
      isValid = false;
    } else {
      errName.classList.add('hidden');
    }

    // Roll Number
    const rollVal = regRollInput.value.trim();
    if (!rollVal) {
      errRoll.classList.remove('hidden');
      isValid = false;
    } else {
      errRoll.classList.add('hidden');
    }

    // Email
    const emailVal = regEmailInput.value.trim();
    if (!emailVal || !emailVal.includes('@') || emailVal.length < 4) {
      errEmail.classList.remove('hidden');
      isValid = false;
    } else {
      errEmail.classList.add('hidden');
    }

    return isValid;
  }

  /**
   * Handle registration form submission.
   */
  regForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    modalAlertError.classList.add('hidden');
    modalAlertSuccess.classList.add('hidden');

    // 1. Client-side checks
    if (!validateForm()) {
      return;
    }

    const eventId = regEventIdInput.value;
    const payload = {
      student_name: regNameInput.value.trim(),
      roll_number: regRollInput.value.trim(),
      email: regEmailInput.value.trim(),
      phone: regPhoneInput.value.trim() || null
    };

    // Show button loading spinner
    submitRegBtn.disabled = true;
    submitBtnSpinner.classList.remove('hidden');
    submitBtnText.textContent = 'Registering...';

    try {
      const response = await fetch(`/api/events/${eventId}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await response.json();

      if (response.status === 201) {
        // SUCCESS
        modalAlertSuccess.classList.remove('hidden');
        modalAlertSuccessMsg.textContent = `You're registered for ${activeEventData ? activeEventData.title : 'this event'}! Registered ID: #${data.id}`;
        submitBtnText.textContent = 'Registered!';

        // Re-fetch events so seat count updates on grid immediately
        fetchEvents();

        // Auto close after 1.8 seconds
        setTimeout(() => {
          closeModal();
        }, 1800);
      } else {
        // FAILURE: Server validation error (400, 404, 409)
        const errorMsg = data.detail || (typeof data === 'string' ? data : 'Registration failed');
        modalAlertError.classList.remove('hidden');
        modalAlertErrorMsg.textContent = errorMsg;
        submitRegBtn.disabled = false;
        submitBtnSpinner.classList.add('hidden');
        submitBtnText.textContent = 'Confirm Registration';
      }
    } catch (err) {
      console.error('Registration network error:', err);
      modalAlertError.classList.remove('hidden');
      modalAlertErrorMsg.textContent = 'Network error connecting to backend. Please check server.';
      submitRegBtn.disabled = false;
      submitBtnSpinner.classList.add('hidden');
      submitBtnText.textContent = 'Confirm Registration';
    }
  });

  // Modal Close Events
  closeModalBtn.addEventListener('click', closeModal);
  modalBackdrop.addEventListener('click', closeModal);
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && !modal.classList.contains('hidden')) {
      closeModal();
    }
  });

  // Category Filter Button Clicks
  categoryFilters.addEventListener('click', (e) => {
    const btn = e.target.closest('.cat-filter-btn');
    if (!btn) return;

    catButtons.forEach(b => {
      b.classList.remove('bg-purple-600', 'text-white', 'shadow-lg', 'shadow-purple-600/30');
      b.classList.add('bg-slate-900', 'text-slate-300', 'border', 'border-slate-800');
    });

    btn.classList.remove('bg-slate-900', 'text-slate-300', 'border', 'border-slate-800');
    btn.classList.add('bg-purple-600', 'text-white', 'shadow-lg', 'shadow-purple-600/30');

    currentCategory = btn.getAttribute('data-category');
    fetchEvents();
  });

  // Search Input with Debounce
  searchInput.addEventListener('input', (e) => {
    searchQuery = e.target.value;
    if (searchQuery.trim().length > 0) {
      clearSearchBtn.classList.remove('hidden');
    } else {
      clearSearchBtn.classList.add('hidden');
    }

    clearTimeout(searchDebounceTimeout);
    searchDebounceTimeout = setTimeout(() => {
      fetchEvents();
    }, 250);
  });

  clearSearchBtn.addEventListener('click', () => {
    searchInput.value = '';
    searchQuery = '';
    clearSearchBtn.classList.add('hidden');
    fetchEvents();
  });

  // Reset Filters button inside empty state
  resetFilterBtn.addEventListener('click', () => {
    searchInput.value = '';
    searchQuery = '';
    clearSearchBtn.classList.add('hidden');
    currentCategory = 'All';

    catButtons.forEach(b => {
      if (b.getAttribute('data-category') === 'All') {
        b.classList.add('bg-purple-600', 'text-white', 'shadow-lg', 'shadow-purple-600/30');
        b.classList.remove('bg-slate-900', 'text-slate-300', 'border');
      } else {
        b.classList.remove('bg-purple-600', 'text-white', 'shadow-lg', 'shadow-purple-600/30');
        b.classList.add('bg-slate-900', 'text-slate-300', 'border', 'border-slate-800');
      }
    });

    fetchEvents();
  });

  retryBtn.addEventListener('click', fetchEvents);

  // Status helper functions
  function showLoading() {
    statusContainer.classList.remove('hidden');
    loadingState.classList.remove('hidden');
    errorState.classList.add('hidden');
    emptyState.classList.add('hidden');
    eventsGrid.classList.add('hidden');
  }

  function showError(msg) {
    statusContainer.classList.remove('hidden');
    loadingState.classList.add('hidden');
    errorState.classList.remove('hidden');
    emptyState.classList.add('hidden');
    errorMessage.textContent = msg;
    eventsGrid.classList.add('hidden');
  }

  function showEmpty() {
    statusContainer.classList.remove('hidden');
    loadingState.classList.add('hidden');
    errorState.classList.add('hidden');
    emptyState.classList.remove('hidden');
    eventsGrid.classList.add('hidden');
  }

  function hideStatus() {
    statusContainer.classList.add('hidden');
    loadingState.classList.add('hidden');
    errorState.classList.add('hidden');
    emptyState.classList.add('hidden');
  }

  function escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  // Initial Load
  fetchEvents();
});
