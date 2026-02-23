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

// Light Mode Colors
private val LightPrimary = Color(0xFF2C3E50)      // Navy Blue (Logo)
private val LightOnPrimary = Color(0xFFFFFFFF)     // White
private val LightPrimaryContainer = Color(0xFFE8EEF5) // Light Navy
private val LightOnPrimaryContainer = Color(0xFF1A2332)

private val LightSecondary = Color(0xFFFCE181)    // Pastel Yellow (Logo)
private val LightOnSecondary = Color(0xFF2C3E50)  // Navy text on yellow
private val LightSecondaryContainer = Color(0xFFFEF7E6)
private val LightOnSecondaryContainer = Color(0xFFD4A742)

private val LightTertiary = Color(0xFF7FE5C8)     // Pastel Green (Logo)
private val LightOnTertiary = Color(0xFFFFFFFF)
private val LightTertiaryContainer = Color(0xFFE6F9F5)
private val LightOnTertiaryContainer = Color(0xFF2B9B7A)

private val LightError = Color(0xFFD32F2F)
private val LightOnError = Color(0xFFFFFFFF)
private val LightErrorContainer = Color(0xFFFFDEDE)
private val LightOnErrorContainer = Color(0xFFC00A0A)

private val LightBackground = Color(0xFFFAFBFC)
private val LightOnBackground = Color(0xFF1C1C1C)
private val LightSurface = Color(0xFFFFFFFF)
private val LightOnSurface = Color(0xFF1C1C1C)
private val LightOutline = Color(0xFF79747E)
private val LightOutlineVariant = Color(0xFFCAC4D0)

// Dark Mode Colors
private val DarkPrimary = Color(0xFFC5D9FF)       // Light Navy for dark mode
private val DarkOnPrimary = Color(0xFF1A2332)     // Dark Navy text
private val DarkPrimaryContainer = Color(0xFF2C3E50) // Original Navy
private val DarkOnPrimaryContainer = Color(0xFFC5D9FF)

private val DarkSecondary = Color(0xFFFCE181)    // Keep yellow for visibility
private val DarkOnSecondary = Color(0xFF2C3E50)  // Navy text on yellow
private val DarkSecondaryContainer = Color(0xFFD4A742)
private val DarkOnSecondaryContainer = Color(0xFFFEF7E6)

private val DarkTertiary = Color(0xFF7FE5C8)     // Keep green for visibility
private val DarkOnTertiary = Color(0xFF1A5C48)
private val DarkTertiaryContainer = Color(0xFF2B9B7A)
private val DarkOnTertiaryContainer = Color(0xFFE6F9F5)

private val DarkError = Color(0xFFD32F2F)
private val DarkOnError = Color(0xFF690005)
private val DarkErrorContainer = Color(0xFFF9DEDC)
private val DarkOnErrorContainer = Color(0xFFF9DEDC)

private val DarkBackground = Color(0xFF121212)
private val DarkOnBackground = Color(0xFFE6E6E6)
private val DarkSurface = Color(0xFF1E1E1E)
private val DarkOnSurface = Color(0xFFE6E6E6)
private val DarkOutline = Color(0xFF938F99)
private val DarkOutlineVariant = Color(0xFF49454E)

/**
 * Light Color Scheme
 */
val LightColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    surfaceVariant = Color(0xFFF5F5F5)
)

/**
 * Dark Color Scheme
 */
val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    surfaceVariant = Color(0xFF49454E)
)

/**
 * ChecklistApp Theme
 * Mendukung light mode, dark mode, dan system theme
 */
@Composable
fun ChecklistAppTheme(
    isDarkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (isDarkMode) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}

/**
 * Helper function untuk mendapatkan color preset yang visible di light/dark mode
 */
object ThemeHelper {
    fun getContrastColor(isDarkMode: Boolean, lightColor: Color, darkColor: Color): Color {
        return if (isDarkMode) darkColor else lightColor
    }
}
