package com.zenith26.fest.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.Emerald400
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Purple950
import com.zenith26.fest.ui.theme.Slate200
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.White
import com.zenith26.fest.ui.theme.ZenithHeadingGradient

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeroSection(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Purple950.copy(alpha = 0.25f))
            .border(width = 1.dp, color = Slate800.copy(alpha = 0.5f))
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pulsing Live Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Slate900.copy(alpha = 0.9f))
                .border(1.dp, Purple600.copy(alpha = 0.4f), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(pulseAlpha)
                        .clip(CircleShape)
                        .background(Emerald400)
                )
                Text(
                    text = "Registrations Now Live · 14 Premier Events",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Purple400
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Headline
        Text(
            text = "Experience the Apex of\nCode, Culture & Competition",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            style = androidx.compose.ui.text.TextStyle(brush = ZenithHeadingGradient)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Subtitle
        Text(
            text = "Zenith '26 unites visionaries, creators, and competitors across 5 action-packed categories. Explore the lineup below and claim your spot before seats run out.",
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = Slate300,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Metadata Info Chips
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetaChip(icon = "📅", text = "November 14 & 15, 2026")
            MetaChip(icon = "📍", text = "Campus Auditorium & Arenas")
            MetaChip(icon = "🎟️", text = "Instant Confirmation")
        }
    }
}

@Composable
private fun MetaChip(icon: String, text: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Slate900.copy(alpha = 0.7f))
            .border(1.dp, Slate800, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = icon, fontSize = 12.sp)
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Slate200
            )
        }
    }
}
