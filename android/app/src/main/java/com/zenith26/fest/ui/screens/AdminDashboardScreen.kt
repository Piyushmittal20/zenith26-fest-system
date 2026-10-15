package com.zenith26.fest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.components.AddEventDialog
import com.zenith26.fest.ui.components.CategoryBreakdown
import com.zenith26.fest.ui.components.EventsTable
import com.zenith26.fest.ui.components.LoadingStateView
import com.zenith26.fest.ui.components.RegistrationsRoster
import com.zenith26.fest.ui.components.StatCards
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Purple900
import com.zenith26.fest.ui.theme.Rose400
import com.zenith26.fest.ui.theme.Rose950
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.White
import com.zenith26.fest.ui.viewmodel.AdminTab
import com.zenith26.fest.ui.viewmodel.FestViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: FestViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.stats.collectAsState()
    val isLoadingStats by viewModel.isLoadingStats.collectAsState()
    val statsError by viewModel.statsError.collectAsState()

    val events by viewModel.events.collectAsState()
    val registrations by viewModel.registrations.collectAsState()
    val selectedEventId by viewModel.selectedRosterEventId.collectAsState()
    val activeTab by viewModel.adminTab.collectAsState()

    val showAddDialog by viewModel.showAddEventDialog.collectAsState()
    val isCreatingEvent by viewModel.isCreatingEvent.collectAsState()
    val addEventError by viewModel.addEventError.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Dashboard Header
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Campus Fest Control Center",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = White
                        )
                        Text(
                            text = "Real-time metrics & registrations.",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }

                    // Refresh Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate900)
                            .border(1.dp, Slate700, RoundedCornerShape(10.dp))
                            .clickable { viewModel.refreshDashboard() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔄", fontSize = 11.sp, modifier = Modifier.padding(end = 4.dp))
                            Text(text = "Refresh", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stretch Goal: Add Event Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Purple600)
                        .clickable { viewModel.setShowAddEventDialog(true) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "➕", fontSize = 12.sp, modifier = Modifier.padding(end = 6.dp))
                        Text(text = "Create New Fest Event", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                    }
                }
            }
        }

        // Error Banner if API failed
        if (statsError != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Rose950)
                        .border(1.dp, Rose400, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Unable to sync live stats", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Rose400)
                            Text(text = statsError ?: "", fontSize = 11.sp, color = Slate300)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Rose400)
                                .clickable { viewModel.fetchStats() }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = "Retry", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White)
                        }
                    }
                }
            }
        }

        // 4 Core Stat Cards
        item {
            StatCards(stats = stats)
        }

        // Category Breakdown
        item {
            CategoryBreakdown(eventsByCategory = stats?.eventsByCategory)
        }

        // Navigation Tabs: Events Table vs Raw Registrations Roster
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isEventsTab = activeTab == AdminTab.EVENTS_TABLE
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isEventsTab) Purple600 else Slate900)
                        .border(1.dp, if (isEventsTab) Purple600 else Slate800, RoundedCornerShape(10.dp))
                        .clickable { viewModel.setAdminTab(AdminTab.EVENTS_TABLE) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎪 Event Utilization",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEventsTab) White else Slate400
                    )
                }

                val isRosterTab = activeTab == AdminTab.REGISTRATIONS_ROSTER
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isRosterTab) Purple600 else Slate900)
                        .border(1.dp, if (isRosterTab) Purple600 else Slate800, RoundedCornerShape(10.dp))
                        .clickable { viewModel.setAdminTab(AdminTab.REGISTRATIONS_ROSTER) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📝 Student Roster",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRosterTab) White else Slate400
                    )
                }
            }
        }

        // Tab Content
        when (activeTab) {
            AdminTab.EVENTS_TABLE -> {
                item {
                    EventsTable(events = events)
                }
            }
            AdminTab.REGISTRATIONS_ROSTER -> {
                item {
                    RegistrationsRoster(
                        registrations = registrations,
                        events = events,
                        selectedEventId = selectedEventId,
                        onEventFilterChanged = { viewModel.setRosterEventFilter(it) },
                        onCancelRegistration = { viewModel.cancelRegistration(it) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Stretch Goal: Add Event Dialog
    if (showAddDialog) {
        AddEventDialog(
            isCreating = isCreatingEvent,
            errorMessage = addEventError,
            onDismiss = { viewModel.setShowAddEventDialog(false) },
            onSubmit = { viewModel.submitCreateEvent(it) }
        )
    }
}
