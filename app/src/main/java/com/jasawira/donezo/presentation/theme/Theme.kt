package com.jasawira.donezo.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * DonezoTheme
 * Main theme wrapper untuk aplikasi Donezo Checklist Manager
 * Mendukung light mode, dark mode, dan dynamic color (Android 12+)
 */
@Composable
fun DonezoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
