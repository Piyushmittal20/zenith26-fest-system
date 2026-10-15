package com.zenith26.fest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.ui.theme.CultBg
import com.zenith26.fest.ui.theme.CultBorder
import com.zenith26.fest.ui.theme.CultText
import com.zenith26.fest.ui.theme.Emerald400
import com.zenith26.fest.ui.theme.Emerald950
import com.zenith26.fest.ui.theme.GamingBg
import com.zenith26.fest.ui.theme.GamingBorder
import com.zenith26.fest.ui.theme.GamingText
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Rose400
import com.zenith26.fest.ui.theme.Rose900
import com.zenith26.fest.ui.theme.Rose950
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.SportsBg
import com.zenith26.fest.ui.theme.SportsBorder
import com.zenith26.fest.ui.theme.SportsText
import com.zenith26.fest.ui.theme.TechBg
import com.zenith26.fest.ui.theme.TechBorder
import com.zenith26.fest.ui.theme.TechText
import com.zenith26.fest.ui.theme.White
import com.zenith26.fest.ui.theme.WorkshopBg
import com.zenith26.fest.ui.theme.WorkshopBorder
import com.zenith26.fest.ui.theme.WorkshopText

@Composable
fun EventCard(
    event: Event,
    onViewDetails: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val isFull = event.availableSeats <= 0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900.copy(alpha = 0.8f))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            // Header: Category Pill & Fee / FULL Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryBadge(category = event.category)

                if (isFull) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Rose950)
                            .border(1.dp, Rose900, RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "FULL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Rose400,
                            letterSpacing = 1.sp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Emerald950.copy(alpha = 0.7f))
                            .border(1.dp, Emerald400.copy(alpha = 0.4f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (event.fee > 0) "₹${event.fee}" else "Free",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Emerald400
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Icon + Title + Description
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate950)
                        .border(1.dp, Slate800, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = event.icon ?: "⚡", fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = event.description ?: "Join us for this exciting campus fest event!",
                        fontSize = 12.sp,
                        color = Slate400,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Date & Venue Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate950.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .border(1.dp, Slate800.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🕒", fontSize = 11.sp, modifier = Modifier.padding(end = 6.dp))
                    Text(
                        text = "${event.eventDate} · ${event.eventTime}",
                        fontSize = 11.sp,
                        color = Slate300
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📍", fontSize = 11.sp, modifier = Modifier.padding(end = 6.dp))
                    Text(
                        text = event.venue,
                        fontSize = 11.sp,
                        color = Slate300,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer: Seats Info + View Details Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isFull) {
                    Text(
                        text = "0 seats available",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Rose400
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${event.availableSeats}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Purple400
                        )
                        Text(
                            text = " / ${event.capacity} seats left",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isFull) Slate800 else Purple600)
                        .clickable { onViewDetails(event) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isFull) "View Info" else "View Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFull) Slate300 else White
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryBadge(category: String) {
    val (bgColor, textColor, borderColor) = when (category.lowercase()) {
        "technical" -> Triple(TechBg, TechText, TechBorder)
        "cultural" -> Triple(CultBg, CultText, CultBorder)
        "sports" -> Triple(SportsBg, SportsText, SportsBorder)
        "gaming" -> Triple(GamingBg, GamingText, GamingBorder)
        "workshop" -> Triple(WorkshopBg, WorkshopText, WorkshopBorder)
        else -> Triple(Slate800, Slate300, Slate700)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = category,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}
