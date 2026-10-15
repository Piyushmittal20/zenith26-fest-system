package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Purple700
import com.zenith26.fest.ui.theme.Purple900
import com.zenith26.fest.ui.theme.Purple950
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.White
import com.zenith26.fest.ui.theme.ZenithHeadingGradient

enum class ScreenSection {
    PORTAL,
    ADMIN
}

@Composable
fun Navbar(
    currentSection: ScreenSection,
    onSectionSelected: (ScreenSection) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate950.copy(alpha = 0.95f))
            .border(width = 1.dp, color = Slate800)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onSectionSelected(ScreenSection.PORTAL) }
            ) {
                Text(
                    text = "⚡",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = "ZENITH '26",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    style = androidx.compose.ui.text.TextStyle(brush = ZenithHeadingGradient)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Purple950.copy(alpha = 0.6f))
                        .border(1.dp, Purple700.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Nov 14–15, 2026",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Purple400
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Switcher Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Events Portal Tab
            val isPortal = currentSection == ScreenSection.PORTAL
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isPortal) Purple900.copy(alpha = 0.5f) else Slate950)
                    .border(
                        1.dp,
                        if (isPortal) Purple600 else Slate700,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSectionSelected(ScreenSection.PORTAL) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Events Portal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isPortal) Purple400 else Slate300
                )
            }

            // Admin Dashboard Tab
            val isAdmin = currentSection == ScreenSection.ADMIN
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isAdmin) Purple900.copy(alpha = 0.5f) else Slate950)
                    .border(
                        1.dp,
                        if (isAdmin) Purple600 else Slate700,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSectionSelected(ScreenSection.ADMIN) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "📊", fontSize = 12.sp)
                    Text(
                        text = "Admin Dashboard",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isAdmin) Purple400 else Slate300
                    )
                }
            }
        }
    }
}
