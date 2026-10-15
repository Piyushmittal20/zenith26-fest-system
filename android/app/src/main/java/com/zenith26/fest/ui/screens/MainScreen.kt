package com.zenith26.fest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenith26.fest.ui.components.Navbar
import com.zenith26.fest.ui.components.ScreenSection
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.viewmodel.FestViewModel

@Composable
fun MainScreen(
    viewModel: FestViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var currentSection by remember { mutableStateOf(ScreenSection.PORTAL) }

    Scaffold(
        topBar = {
            Navbar(
                currentSection = currentSection,
                onSectionSelected = { currentSection = it }
            )
        },
        bottomBar = {
            // Footer matching web design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate950)
                    .border(width = 1.dp, color = Slate900)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Zenith '26 College Fest · Kotlin & FastAPI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate400
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "PM2-01 & PM2-02 Technology & Agile Execution",
                        fontSize = 10.sp,
                        color = Slate800
                    )
                }
            }
        },
        containerColor = Slate950
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentSection) {
                ScreenSection.PORTAL -> StudentPortalScreen(viewModel = viewModel)
                ScreenSection.ADMIN -> AdminDashboardScreen(viewModel = viewModel)
            }
        }
    }
}
