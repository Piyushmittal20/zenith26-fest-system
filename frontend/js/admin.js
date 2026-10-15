/**
 * Zenith '26 College Fest Management System
 * Admin Dashboard JavaScript (admin.js)
 */

document.addEventListener('DOMContentLoaded', () => {
  // State
  let statsData = null;
  let eventsData = [];
  let registrationsData = [];
  let activeTab = 'events'; // 'events' | 'registrations'

  // DOM Elements - Top Stats
  const statTotalEvents = document.getElementById('stat-total-events');
  const statTotalRegs = document.getElementById('stat-total-regs');
  const statUtilizationPct = document.getElementById('stat-utilization-pct');
  const statCapacitySubtext = document.getElementById('stat-capacity-subtext');
  const statUpcomingEvents = document.getElementById('stat-upcoming-events');
  const categoryBreakdownContainer = document.getElementById('category-breakdown-container');
  const refreshBtn = document.getElementById('refresh-dashboard-btn');
  const refreshSpinner = document.getElementById('refresh-spinner');

  // Error Banner
  const adminErrorBanner = document.getElementById('admin-error-banner');
  const adminErrorMsg = document.getElementById('admin-error-msg');
  const adminRetryBtn = document.getElementById('admin-retry-btn');

  // Tabs & Sections
  const tabEventsBtn = document.getElementById('tab-events-btn');
  const tabRegsBtn = document.getElementById('tab-regs-btn');
  const sectionEventsTable = document.getElementById('section-events-table');
  const sectionRegistrations = document.getElementById('section-registrations');
  const tableEventsCount = document.getElementById('table-events-count');
  const eventsTableBody = document.getElementById('events-table-body');

  // Registrations Roster
  const regFilterEvent = document.getElementById('reg-filter-event');
  const regsCountBadge = document.getElementById('regs-count-badge');
  const registrationsTableBody = document.getElementById('registrations-table-body');

  // Add Event Modal (Stretch Goal)
  const addEventBtn = document.getElementById('add-event-btn');
  const addEventModal = document.getElementById('add-event-modal');
  const addModalBackdrop = document.getElementById('add-modal-backdrop');
  const closeAddModalBtn = document.getElementById('close-add-modal-btn');
  const addEventForm = document.getElementById('add-event-form');
  const addEventError = document.getElementById('add-event-error');

  /**
   * Category Style Helper
   */
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
   * Fetch and update stats from /api/stats
   */
  async function loadStats() {
    try {
      const response = await fetch('/api/stats');
      if (!response.ok) throw new Error(`Stats fetch failed (${response.status})`);
      statsData = await response.json();

      statTotalEvents.textContent = statsData.total_events;
      statTotalRegs.textContent = statsData.total_registrations;
      statUtilizationPct.textContent = `${statsData.overall_utilization_pct}%`;
      statCapacitySubtext.textContent = `of ${statsData.total_capacity} total fest seats`;
      statUpcomingEvents.textContent = statsData.upcoming_events_count;

      renderCategoryBreakdown(statsData.events_by_category, statsData.total_events);
      hideAdminError();
    } catch (err) {
      console.error(err);
      showAdminError('Failed to load live fest statistics. Verify server is online.');
    }
  }

  /**
   * Render Category Distribution Cards
   */
  function renderCategoryBreakdown(byCategory, total) {
    categoryBreakdownContainer.innerHTML = '';
    if (!byCategory) return;

    for (const [cat, count] of Object.entries(byCategory)) {
      const pct = total > 0 ? Math.round((count / total) * 100) : 0;
      const card = document.createElement('div');
      card.className = 'bg-slate-900/80 border border-slate-800 p-3 rounded-xl flex flex-col justify-between';
      card.innerHTML = `
        <div class="flex items-center justify-between">
          <span class="text-xs font-semibold text-slate-300">${escapeHtml(cat)}</span>
          <span class="text-xs font-bold text-purple-400">${count}</span>
        </div>
        <div class="mt-2.5 w-full bg-slate-950 rounded-full h-1.5 overflow-hidden border border-slate-800">
          <div class="bg-gradient-to-r from-purple-500 to-indigo-500 h-1.5 rounded-full" style="width: ${pct}%"></div>
        </div>
        <span class="text-[10px] text-slate-500 mt-1 block">${pct}% of lineup</span>
      `;
      categoryBreakdownContainer.appendChild(card);
    }
  }

  /**
   * Fetch and render events table sorted by utilization descending
   */
  async function loadEventsTable() {
    try {
      const response = await fetch('/api/events');
      if (!response.ok) throw new Error(`Events fetch failed (${response.status})`);
      eventsData = await response.json();

      tableEventsCount.textContent = eventsData.length;
      populateEventFilterDropdown(eventsData);

      // Compute registered count & utilization percentage for each event
      const eventsWithMetrics = eventsData.map(e => {
        const registered = e.capacity - e.available_seats;
        const utilPct = e.capacity > 0 ? (registered / e.capacity) * 100 : 0;
        return { ...e, registered, utilPct: Math.round(utilPct * 10) / 10 };
      });

      // Sort by utilization descending (highest demand first)
      eventsWithMetrics.sort((a, b) => b.utilPct - a.utilPct);

      eventsTableBody.innerHTML = '';

      if (eventsWithMetrics.length === 0) {
        eventsTableBody.innerHTML = `
          <tr>
            <td colspan="7" class="py-8 text-center text-slate-400">No events found in database.</td>
          </tr>
        `;
        return;
      }

      eventsWithMetrics.forEach(ev => {
        const isHighDemand = ev.utilPct >= 90;
        const isFull = ev.available_seats <= 0;

        const row = document.createElement('tr');
        row.className = `hover:bg-slate-800/40 transition-colors ${
          isHighDemand ? 'bg-amber-950/20' : ''
        }`;

        row.innerHTML = `
          <td class="py-3.5 px-4 font-semibold text-white">
            <div class="flex items-center gap-2.5">
              <span class="text-lg">${ev.icon || '🎪'}</span>
              <div>
                <span>${escapeHtml(ev.title)}</span>
                ${
                  isHighDemand
                    ? '<span class="ml-2 px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-950 text-amber-300 border border-amber-700/80 animate-pulse">⚡ &gt;90% FULL</span>'
                    : ''
                }
              </div>
            </div>
          </td>
          <td class="py-3.5 px-4">
            <span class="px-2 py-0.5 rounded-full text-xs font-semibold border ${getCategoryStyles(ev.category)}">
              ${escapeHtml(ev.category)}
            </span>
          </td>
          <td class="py-3.5 px-4 text-slate-300 whitespace-nowrap">
            ${escapeHtml(ev.event_date)} · ${escapeHtml(ev.event_time)}
          </td>
          <td class="py-3.5 px-4 text-slate-300">
            ${escapeHtml(ev.venue)}
          </td>
          <td class="py-3.5 px-4 text-center">
            <span class="font-bold ${isFull ? 'text-rose-400' : 'text-slate-200'}">
              ${ev.registered}
            </span>
            <span class="text-slate-500"> / ${ev.capacity}</span>
          </td>
          <td class="py-3.5 px-4 text-center">
            <div class="inline-flex items-center gap-2">
              <div class="w-16 bg-slate-950 rounded-full h-2 overflow-hidden border border-slate-800">
                <div class="h-2 rounded-full ${
                  isFull ? 'bg-rose-500' : isHighDemand ? 'bg-amber-400' : 'bg-purple-500'
                }" style="width: ${Math.min(100, ev.utilPct)}%"></div>
              </div>
              <span class="font-bold text-xs ${
                isFull ? 'text-rose-400' : isHighDemand ? 'text-amber-300' : 'text-slate-200'
              }">${ev.utilPct}%</span>
            </div>
          </td>
          <td class="py-3.5 px-4 text-right">
            <button 
              data-filter-id="${ev.id}" 
              class="view-reg-btn text-xs text-purple-400 hover:text-purple-300 font-semibold underline underline-offset-4"
            >
              View Roster
            </button>
          </td>
        `;

        eventsTableBody.appendChild(row);
      });

      // Quick filter jump
      document.querySelectorAll('.view-reg-btn').forEach(btn => {
        btn.addEventListener('click', () => {
          const id = btn.getAttribute('data-filter-id');
          regFilterEvent.value = id;
          switchTab('registrations');
          loadRegistrations(id);
        });
      });

      hideAdminError();
    } catch (err) {
      console.error(err);
      showAdminError('Failed to load events table.');
    }
  }

  /**
   * Populate event filter dropdown
   */
  function populateEventFilterDropdown(events) {
    const currentVal = regFilterEvent.value;
    regFilterEvent.innerHTML = '<option value="">All Events</option>';
    events.forEach(e => {
      const opt = document.createElement('option');
      opt.value = e.id;
      opt.textContent = `${e.title} (${e.category})`;
      regFilterEvent.appendChild(opt);
    });
    regFilterEvent.value = currentVal;
  }

  /**
   * Fetch and render registrations from /api/registrations
   */
  async function loadRegistrations(eventIdFilter = '') {
    try {
      let url = '/api/registrations';
      if (eventIdFilter && eventIdFilter.trim() !== '') {
        url += `?event_id=${encodeURIComponent(eventIdFilter)}`;
      }

      const response = await fetch(url);
      if (!response.ok) throw new Error(`Registrations fetch failed (${response.status})`);
      registrationsData = await response.json();

      regsCountBadge.textContent = registrationsData.length;
      registrationsTableBody.innerHTML = '';

      if (registrationsData.length === 0) {
        registrationsTableBody.innerHTML = `
          <tr>
            <td colspan="7" class="py-10 text-center text-slate-400">
              No registrations found for this selection.
            </td>
          </tr>
        `;
        return;
      }

      registrationsData.forEach(reg => {
        const formattedDate = reg.registered_at ? new Date(reg.registered_at).toLocaleString() : '--';
        const row = document.createElement('tr');
        row.className = 'hover:bg-slate-800/40 transition-colors';

        row.innerHTML = `
          <td class="py-3 px-4 font-mono text-xs text-slate-400">#${reg.id}</td>
          <td class="py-3 px-4 font-semibold text-white">${escapeHtml(reg.student_name)}</td>
          <td class="py-3 px-4 text-purple-300 font-mono text-xs">${escapeHtml(reg.roll_number)}</td>
          <td class="py-3 px-4 text-slate-300">${escapeHtml(reg.email)}</td>
          <td class="py-3 px-4">
            <span class="font-medium text-slate-200">${escapeHtml(reg.event_title)}</span>
          </td>
          <td class="py-3 px-4 text-slate-400 text-xs whitespace-nowrap">${escapeHtml(formattedDate)}</td>
          <td class="py-3 px-4 text-right">
            <button 
              data-delete-id="${reg.id}" 
              data-student="${escapeHtml(reg.student_name)}" 
              class="cancel-reg-btn px-2.5 py-1 text-xs font-semibold rounded-lg bg-rose-950/60 hover:bg-rose-900 text-rose-300 border border-rose-800/60 transition-colors"
            >
              Cancel
            </button>
          </td>
        `;

        registrationsTableBody.appendChild(row);
      });

      // Attach cancel listeners (Stretch Goal)
      document.querySelectorAll('.cancel-reg-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
          const regId = btn.getAttribute('data-delete-id');
          const student = btn.getAttribute('data-student');
          if (confirm(`Are you sure you want to cancel the registration for ${student} (ID #${regId})?`)) {
            await deleteRegistration(regId);
          }
        });
      });

      hideAdminError();
    } catch (err) {
      console.error(err);
      showAdminError('Failed to load student registrations.');
    }
  }

  /**
   * Delete registration (Stretch Goal)
   */
  async function deleteRegistration(regId) {
    try {
      const response = await fetch(`/api/registrations/${regId}`, {
        method: 'DELETE'
      });
      if (!response.ok) {
        const err = await response.json();
        throw new Error(err.detail || 'Failed to cancel registration');
      }

      // Refresh data immediately
      await loadStats();
      await loadEventsTable();
      await loadRegistrations(regFilterEvent.value);
    } catch (err) {
      alert(`Error cancelling registration: ${err.message}`);
    }
  }

  /**
   * Tab Switching
   */
  function switchTab(tab) {
    activeTab = tab;
    if (tab === 'events') {
      tabEventsBtn.className = 'px-4 py-2 text-sm font-bold rounded-xl transition-all bg-purple-600 text-white shadow-lg shadow-purple-600/30';
      tabRegsBtn.className = 'px-4 py-2 text-sm font-semibold rounded-xl text-slate-400 hover:text-white hover:bg-slate-900 transition-all';
      sectionEventsTable.classList.remove('hidden');
      sectionRegistrations.classList.add('hidden');
    } else {
      tabRegsBtn.className = 'px-4 py-2 text-sm font-bold rounded-xl transition-all bg-purple-600 text-white shadow-lg shadow-purple-600/30';
      tabEventsBtn.className = 'px-4 py-2 text-sm font-semibold rounded-xl text-slate-400 hover:text-white hover:bg-slate-900 transition-all';
      sectionEventsTable.classList.add('hidden');
      sectionRegistrations.classList.remove('hidden');
    }
  }

  tabEventsBtn.addEventListener('click', () => switchTab('events'));
  tabRegsBtn.addEventListener('click', () => {
    switchTab('registrations');
    loadRegistrations(regFilterEvent.value);
  });

  regFilterEvent.addEventListener('change', () => {
    loadRegistrations(regFilterEvent.value);
  });

  // Refresh Button Click
  refreshBtn.addEventListener('click', async () => {
    refreshSpinner.classList.add('rotate-180');
    await loadStats();
    await loadEventsTable();
    if (activeTab === 'registrations') {
      await loadRegistrations(regFilterEvent.value);
    }
    setTimeout(() => {
      refreshSpinner.classList.remove('rotate-180');
    }, 400);
  });

  // Add Event Modal (Stretch Goal)
  addEventBtn.addEventListener('click', () => {
    addEventModal.classList.remove('hidden');
    addEventError.classList.add('hidden');
    addEventForm.reset();
  });

  closeAddModalBtn.addEventListener('click', () => {
    addEventModal.classList.add('hidden');
  });

  addModalBackdrop.addEventListener('click', () => {
    addEventModal.classList.add('hidden');
  });

  addEventForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    addEventError.classList.add('hidden');

    const payload = {
      title: document.getElementById('new-title').value.trim(),
      category: document.getElementById('new-category').value,
      icon: document.getElementById('new-icon').value.trim() || '🎪',
      event_date: document.getElementById('new-date').value,
      event_time: document.getElementById('new-time').value.trim(),
      venue: document.getElementById('new-venue').value.trim(),
      capacity: parseInt(document.getElementById('new-capacity').value, 10),
      fee: parseInt(document.getElementById('new-fee').value, 10) || 0,
      description: document.getElementById('new-desc').value.trim() || null
    };

    try {
      const response = await fetch('/api/events', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await response.json();
      if (response.status === 201) {
        addEventModal.classList.add('hidden');
        await loadStats();
        await loadEventsTable();
      } else {
        addEventError.classList.remove('hidden');
        addEventError.textContent = data.detail || 'Could not create event';
      }
    } catch (err) {
      addEventError.classList.remove('hidden');
      addEventError.textContent = 'Network error saving event';
    }
  });

  // Error Banner helpers
  function showAdminError(msg) {
    adminErrorBanner.classList.remove('hidden');
    adminErrorMsg.textContent = msg;
  }

  function hideAdminError() {
    adminErrorBanner.classList.add('hidden');
  }

  adminRetryBtn.addEventListener('click', () => {
    loadStats();
    loadEventsTable();
  });

  function escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  // Initial dashboard load
  loadStats();
  loadEventsTable();
  loadRegistrations();
});
