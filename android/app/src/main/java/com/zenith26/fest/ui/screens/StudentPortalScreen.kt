package com.zenith26.fest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zenith26.fest.ui.components.CategoryFilters
import com.zenith26.fest.ui.components.EmptyStateView
import com.zenith26.fest.ui.components.ErrorStateView
import com.zenith26.fest.ui.components.EventCard
import com.zenith26.fest.ui.components.HeroSection
import com.zenith26.fest.ui.components.LoadingStateView
import com.zenith26.fest.ui.components.RegistrationDialog
import com.zenith26.fest.ui.components.SearchBar
import com.zenith26.fest.ui.viewmodel.FestViewModel

@Composable
fun StudentPortalScreen(
    viewModel: FestViewModel,
    modifier: Modifier = Modifier
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoadingEvents.collectAsState()
    val errorMsg by viewModel.eventsError.collectAsState()
    val currentCategory by viewModel.currentCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val activeModalEvent by viewModel.activeModalEvent.collectAsState()
    val isRegistering by viewModel.isRegistering.collectAsState()
    val regSuccessMsg by viewModel.registrationSuccessMsg.collectAsState()
    val regErrorMsg by viewModel.registrationErrorMsg.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Section
        item {
            HeroSection()
        }

        // Search Input
        item {
            SearchBar(
                query = searchQuery,
                onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                onClearQuery = { viewModel.clearSearch() }
            )
        }

        // Category Filter Pills
        item {
            CategoryFilters(
                selectedCategory = currentCategory,
                onCategorySelected = { viewModel.selectCategory(it) }
            )
        }

        // Feedback States: Loading / Error / Empty / Events List
        when {
            isLoading -> {
                item {
                    LoadingStateView()
                }
            }
            errorMsg != null -> {
                item {
                    ErrorStateView(
                        errorMessage = errorMsg ?: "Unable to load events.",
                        onRetry = { viewModel.fetchEvents() }
                    )
                }
            }
            events.isEmpty() -> {
                item {
                    EmptyStateView(
                        onReset = { viewModel.resetAllFilters() }
                    )
                }
            }
            else -> {
                items(events, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        onViewDetails = { viewModel.openEventModal(it) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Event Detail & Registration Dialog
    if (activeModalEvent != null) {
        RegistrationDialog(
            event = activeModalEvent!!,
            isRegistering = isRegistering,
            successMessage = regSuccessMsg,
            errorMessage = regErrorMsg,
            onDismiss = { viewModel.closeEventModal() },
            onSubmit = { name, roll, email, phone ->
                viewModel.submitRegistration(name, roll, email, phone)
            }
        )
    }
}
