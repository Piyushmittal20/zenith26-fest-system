package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.zenith26.fest.data.model.StatsOut
import com.zenith26.fest.ui.theme.Amber400
import com.zenith26.fest.ui.theme.Amber950
import com.zenith26.fest.ui.theme.Cyan400
import com.zenith26.fest.ui.theme.Cyan950
import com.zenith26.fest.ui.theme.Emerald400
import com.zenith26.fest.ui.theme.Emerald950
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple950
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.White

@Composable
fun StatCards(stats: StatsOut?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SingleStatCard(
                title = "TOTAL EVENTS",
                value = stats?.totalEvents?.toString() ?: "--",
                subtext = "5 active categories",
                subtextColor = Purple400,
                icon = "🎪",
                iconBg = Purple950,
                modifier = Modifier.weight(1f)
            )
            SingleStatCard(
                title = "TOTAL REGISTRATIONS",
                value = stats?.totalRegistrations?.toString() ?: "--",
                subtext = "Live student enrollments",
                subtextColor = Emerald400,
                icon = "👥",
                iconBg = Emerald950,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SingleStatCard(
                title = "CAPACITY UTILIZATION",
                value = if (stats != null) "${stats.overallUtilizationPct}%" else "--%",
                subtext = "of ${stats?.totalCapacity ?: "--"} total seats",
                subtextColor = Amber400,
                icon = "📈",
                iconBg = Amber950,
                modifier = Modifier.weight(1f)
            )
            SingleStatCard(
                title = "UPCOMING EVENTS",
                value = stats?.upcomingEventsCount?.toString() ?: "--",
                subtext = "Nov 14 & 15 scheduled",
                subtextColor = Cyan400,
                icon = "📅",
                iconBg = Cyan950,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SingleStatCard(
    title: String,
    value: String,
    subtext: String,
    subtextColor: Color,
    icon: String,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900.copy(alpha = 0.8f))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtext,
                    fontSize = 10.sp,
                    color = subtextColor
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg.copy(alpha = 0.7f))
                    .border(1.dp, Slate800, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 20.sp)
            }
        }
    }
}
