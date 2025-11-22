package com.example.petservicetemp.ui.theme

import androidx.compose.ui.graphics.Color

// Unified Theme Colors
val Primary = Color(0xFF819067)
val PrimaryDark = Color(0xFF404C35)
val PrimaryVariant = Color(0xFF5E744A)
val BackgroundLight = Color(0xFFF8F8F8)
val Secondary = Color(0xFFD9CBA3)

// Status Colors
val StatusPending = Color(0xFFFF9800)
val StatusConfirmed = Color(0xFF4CAF50)
val StatusCompleted = Color(0xFF2196F3)
val StatusCancelled = Color(0xFFF44336)

// Legacy colors for compatibility
val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

// Color Map
val ColorMap = mapOf(
    // Unified Theme Colors
    "Primary" to Primary,
    "PrimaryDark" to PrimaryDark,
    "PrimaryVariant" to PrimaryVariant,
    "BackgroundLight" to BackgroundLight,
    "Secondary" to Secondary,
    
    // Status Colors
    "StatusPending" to StatusPending,
    "StatusConfirmed" to StatusConfirmed,
    "StatusCompleted" to StatusCompleted,
    "StatusCancelled" to StatusCancelled,
    
    // Legacy colors
    "Purple200" to Purple200,
    "Purple500" to Purple500,
    "Purple700" to Purple700,
    "Teal200" to Teal200
)