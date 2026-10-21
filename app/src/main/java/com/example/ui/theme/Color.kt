package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Deep Purple & Variants
val PurplePrimary = Color(0xFF7C3AED)
val PurplePrimaryDark = Color(0xFF6D28D9)
val PurplePrimaryLight = Color(0xFF8B5CF6)
val PurpleContainer = Color(0xFFF3E8FF)
val OnPurpleContainer = Color(0xFF4C1D95)

// Pink Accent for Gradient
val PinkAccent = Color(0xFFEC4899)
val PinkAccentLight = Color(0xFFF472B6)
val PinkContainer = Color(0xFFFCE7F3)
val OnPinkContainer = Color(0xFF831843)

// Success Gold Accent for Completed Goals & Celebrations
val GoldSuccess = Color(0xFFF59E0B)
val GoldSuccessLight = Color(0xFFFCD34D)
val GoldContainer = Color(0xFFFEF3C7)
val OnGoldContainer = Color(0xFF78350F)

// Background & Surfaces
val BackgroundLight = Color(0xFFFAFAFA)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF4F3F8)
val TextPrimaryLight = Color(0xFF1E1B2E)
val TextSecondaryLight = Color(0xFF6B7280)
val TextMutedLight = Color(0xFF9CA3AF)
val BorderLight = Color(0xFFE5E7EB)

// Dark Theme Colors
val BackgroundDark = Color(0xFF0F0D17)
val SurfaceDark = Color(0xFF1A1726)
val SurfaceVariantDark = Color(0xFF252136)
val TextPrimaryDark = Color(0xFFF9FAFB)
val TextSecondaryDark = Color(0xFFD1D5DB)
val BorderDark = Color(0xFF322C47)

// Custom Gradients
val PurplePinkGradient = Brush.horizontalGradient(
    colors = listOf(PurplePrimary, PinkAccent)
)

val PurplePinkVerticalGradient = Brush.verticalGradient(
    colors = listOf(PurplePrimaryLight, PinkAccent)
)

val GoldCelebrationGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFF59E0B), Color(0xFFFBBF24), Color(0xFFFDE68A))
)

val CardGlowGradient = Brush.radialGradient(
    colors = listOf(Color(0x227C3AED), Color(0x00FFFFFF))
)
