package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.RegistrationDetail
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Rose400
import com.zenith26.fest.ui.theme.Rose950
import com.zenith26.fest.ui.theme.Slate200
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.White

@Composable
fun RegistrationsRoster(
    registrations: List<RegistrationDetail>,
    events: List<Event>,
    selectedEventId: Int?,
    onEventFilterChanged: (Int?) -> Unit,
    onCancelRegistration: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Event Filter Selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Slate900.copy(alpha = 0.8f))
                .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "FILTER BY EVENT:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // "All Events" option
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedEventId == null) Purple600 else Slate950)
                        .border(1.dp, if (selectedEventId == null) Purple600 else Slate800, RoundedCornerShape(8.dp))
                        .clickable { onEventFilterChanged(null) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "All Events",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedEventId == null) White else Slate300
                    )
                }

                events.forEach { ev ->
                    val isSelected = selectedEventId == ev.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Purple600 else Slate950)
                            .border(1.dp, if (isSelected) Purple600 else Slate800, RoundedCornerShape(8.dp))
                            .clickable { onEventFilterChanged(ev.id) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${ev.icon ?: "⚡"} ${ev.title}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) White else Slate300
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Showing ${registrations.size} registrations",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Purple400
            )
        }

        if (registrations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Slate900)
                    .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No registrations recorded for this selection.",
                    fontSize = 12.sp,
                    color = Slate400
                )
            }
        } else {
            registrations.forEach { reg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Slate900.copy(alpha = 0.8f))
                        .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${reg.id}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Purple400,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = reg.studentName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }

                            // Stretch goal: Cancel registration
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Rose950)
                                    .border(1.dp, Rose400.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { onCancelRegistration(reg.id) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Rose400
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Event: ${reg.eventTitle}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate200
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Roll: ${reg.rollNumber} · ${reg.email}",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                        }

                        if (!reg.phone.isNullOrBlank()) {
                            Text(
                                text = "Phone: ${reg.phone}",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Registered: ${reg.registeredAt.take(19).replace('T', ' ')}",
                            fontSize = 10.sp,
                            color = Slate400
                        )
                    }
                }
            }
        }
    }
}
