package com.jasawira.donezo.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * THEME COLORS
 * Brand color mengikuti logo Donezo: Navy Blue (Primary), Yellow accent
 */

/**
 * Light Color Scheme
 */
val LightColors = lightColorScheme(
    primary = Color(0xFF2C3E50), // Navy
    onPrimary = Color.White,

    secondary = Color(0xFFFCE181),
    onSecondary = Color(0xFF2C3E50),

    tertiary = Color(0xFF7FE5C8),
    onTertiary = Color.Black,

    background = Color(0xFFF7F9FC),
    onBackground = Color(0xFF1C1C1C),

    surface = Color.White,
    onSurface = Color(0xFF1C1C1C),

    surfaceVariant = Color(0xFFF1F3F6),
    onSurfaceVariant = Color(0xFF6B7280),

    outline = Color(0xFFE5E7EB)
)

