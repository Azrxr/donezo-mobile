package com.jasawira.donezo.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * THEME COLORS
 * Brand color menggunakan Pastel Pink sebagai primary
 */

// Light Mode Colors
private val LightPrimary = Color(0xFFFFB3D9)      // Pastel Pink
private val LightOnPrimary = Color(0xFFFFFFFF)     // White
private val LightPrimaryContainer = Color(0xFFFFE5F0)
private val LightOnPrimaryContainer = Color(0xFFC2185B)

private val LightSecondary = Color(0xFFB3E5FC)    // Pastel Blue
private val LightOnSecondary = Color(0xFF0277BD)
private val LightSecondaryContainer = Color(0xFFE1F5FE)
private val LightOnSecondaryContainer = Color(0xFF0277BD)

private val LightTertiary = Color(0xFFC8E6C9)     // Pastel Green
private val LightOnTertiary = Color(0xFFFFFFFF)
private val LightTertiaryContainer = Color(0xFFE8F5E9)
private val LightOnTertiaryContainer = Color(0xFF388E3C)

private val LightError = Color(0xFFD32F2F)
private val LightOnError = Color(0xFFFFFFFF)
private val LightErrorContainer = Color(0xFFFFDEDE)
private val LightOnErrorContainer = Color(0xFFC00A0A)

private val LightBackground = Color(0xFFFAFAFA)
private val LightOnBackground = Color(0xFF1C1C1C)
private val LightSurface = Color(0xFFFFFFFF)
private val LightOnSurface = Color(0xFF1C1C1C)
private val LightOutline = Color(0xFF79747E)

// Dark Mode Colors
private val DarkPrimary = Color(0xFFFFB3D9)       // Keep pastel pink
private val DarkOnPrimary = Color(0xFFC2185B)
private val DarkPrimaryContainer = Color(0xFFFFCCE5)
private val DarkOnPrimaryContainer = Color(0xFF7B1FA2)

private val DarkSecondary = Color(0xFFB3E5FC)    // Keep pastel blue
private val DarkOnSecondary = Color(0xFF0277BD)
private val DarkSecondaryContainer = Color(0xFF80DEEA)
private val DarkOnSecondaryContainer = Color(0xFF01579B)

private val DarkTertiary = Color(0xFFC8E6C9)     // Keep pastel green
private val DarkOnTertiary = Color(0xFF388E3C)
private val DarkTertiaryContainer = Color(0xFFE0F2E1)
private val DarkOnTertiaryContainer = Color(0xFF1B5E20)

private val DarkError = Color(0xFFD32F2F)
private val DarkOnError = Color(0xFF690005)
private val DarkErrorContainer = Color(0xFFF9DEDC)
private val DarkOnErrorContainer = Color(0xFFF9DEDC)

private val DarkBackground = Color(0xFF121212)
private val DarkOnBackground = Color(0xFFE6E6E6)
private val DarkSurface = Color(0xFF1E1E1E)
private val DarkOnSurface = Color(0xFFE6E6E6)
private val DarkOutline = Color(0xFF938F99)

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
