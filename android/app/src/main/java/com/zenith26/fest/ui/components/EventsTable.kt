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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.ui.theme.Amber400
import com.zenith26.fest.ui.theme.Amber950
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Rose400
import com.zenith26.fest.ui.theme.Rose950
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.White

@Composable
fun EventsTable(
    events: List<Event>,
    modifier: Modifier = Modifier
) {
    // Sort events by utilization descending
    val sortedEvents = events.sortedByDescending { ev ->
        val registered = ev.capacity - ev.availableSeats
        if (ev.capacity > 0) (registered.toDouble() / ev.capacity.toDouble()) else 0.0
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Table Header Info Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Slate900.copy(alpha = 0.5f))
                .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sort: Highest utilization descending",
                    fontSize = 11.sp,
                    color = Slate300
                )
                Text(
                    text = "Total Events: ${events.size}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }

        // Table Rows / Cards
        sortedEvents.forEach { event ->
            val registered = event.capacity - event.availableSeats
            val utilPct = if (event.capacity > 0) {
                Math.round((registered.toDouble() / event.capacity.toDouble()) * 1000.0) / 10.0
            } else 0.0
            val isHighDemand = utilPct >= 90.0

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Slate900.copy(alpha = 0.8f))
                    .border(
                        1.dp,
                        if (isHighDemand) Amber400.copy(alpha = 0.8f) else Slate800,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = event.icon ?: "⚡", fontSize = 16.sp, modifier = Modifier.padding(end = 6.dp))
                            Text(
                                text = event.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Utilization Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isHighDemand) Amber950 else Slate950)
                                .border(
                                    1.dp,
                                    if (isHighDemand) Amber400 else Slate800,
                                    RoundedCornerShape(50)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isHighDemand) "⚠️ $utilPct%" else "$utilPct%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHighDemand) Amber400 else Purple400
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryBadge(category = event.category)
                        Text(
                            text = "${event.eventDate} · ${event.eventTime}",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📍 ${event.venue}",
                            fontSize = 11.sp,
                            color = Slate300,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Registered: $registered / ${event.capacity}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate200
                        )
                    }
                }
            }
        }
    }
}
