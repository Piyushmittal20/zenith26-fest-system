package com.zenith26.fest.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Background and Surface Slates (Tailwind slate-950, 900, 800, 700)
val Slate950 = Color(0xFF020617)
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate600 = Color(0xFF475569)
val Slate400 = Color(0xFF94A3B8)
val Slate300 = Color(0xFFCBD5E1)
val Slate200 = Color(0xFFE2E8F0)
val Slate100 = Color(0xFFF8FAFC)
val White = Color(0xFFFFFFFF)

// Brand Purple Accents
val Purple400 = Color(0xFFC084FC)
val Purple500 = Color(0xFF8B5CF6)
val Purple600 = Color(0xFF7C3AED)
val Purple700 = Color(0xFF6D28D9)
val Purple900 = Color(0xFF3B0764)
val Purple950 = Color(0xFF2E1065)

// Category Badge Colors
// Technical (Blue)
val TechBg = Color(0xFF1E3A8A).copy(alpha = 0.6f)
val TechText = Color(0xFF93C5FD)
val TechBorder = Color(0xFF1D4ED8).copy(alpha = 0.5f)

// Cultural (Pink)
val CultBg = Color(0xFF831843).copy(alpha = 0.6f)
val CultText = Color(0xFFF472B6)
val CultBorder = Color(0xFFBE185D).copy(alpha = 0.5f)

// Sports (Emerald)
val SportsBg = Color(0xFF064E3B).copy(alpha = 0.6f)
val SportsText = Color(0xFF6EE7B7)
val SportsBorder = Color(0xFF047857).copy(alpha = 0.5f)

// Gaming (Purple)
val GamingBg = Color(0xFF581C87).copy(alpha = 0.6f)
val GamingText = Color(0xFFD8B4FE)
val GamingBorder = Color(0xFF7E22CE).copy(alpha = 0.5f)

// Workshop (Amber)
val WorkshopBg = Color(0xFF78350F).copy(alpha = 0.6f)
val WorkshopText = Color(0xFFFCD34D)
val WorkshopBorder = Color(0xFFB45309).copy(alpha = 0.5f)

// Status Colors
val Emerald950 = Color(0xFF022C22)
val Emerald400 = Color(0xFF34D399)
val Emerald500 = Color(0xFF10B981)

val Rose950 = Color(0xFF4C0519)
val Rose900 = Color(0xFF881337)
val Rose400 = Color(0xFFFB7185)
val Rose500 = Color(0xFFF43F5E)
val Rose600 = Color(0xFFE11D48)

val Amber950 = Color(0xFF451A03)
val Amber400 = Color(0xFFFBBF24)
val Amber300 = Color(0xFFFCD34D)

val Cyan950 = Color(0xFF083344)
val Cyan400 = Color(0xFF22D3EE)

// Gradient Brushes
val ZenithHeadingGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFC084FC), // Purple-400
        Color(0xFFF472B6), // Pink-400
        Color(0xFFFCD34D)  // Amber-300
    )
)

val ZenithButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF7C3AED), // Purple-600
        Color(0xFF4F46E5)  // Indigo-600
    )
)
