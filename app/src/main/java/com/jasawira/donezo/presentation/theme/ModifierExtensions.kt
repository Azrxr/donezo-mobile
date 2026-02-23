package com.jasawira.donezo.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

/**
 * ModifierExtensions
 * Helper functions untuk menambahkan shadow dan border ke components
 */

/**
 * Tambahkan subtle shadow ke card
 */
@Composable
fun Modifier.cardShadow(): Modifier {
    return this.shadow(
        elevation = 4.dp,
        shape = MaterialTheme.shapes.medium,
        spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
    )
}

/**
 * Tambahkan shadow yang lebih prominent
 */
@Composable
fun Modifier.prominentShadow(): Modifier {
    return this.shadow(
        elevation = 8.dp,
        shape = MaterialTheme.shapes.medium,
        spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    )
}

/**
 * Subtle shadow untuk elements
 */
@Composable
fun Modifier.subtleShadow(): Modifier {
    return this.shadow(
        elevation = 2.dp,
        shape = MaterialTheme.shapes.small,
        spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    )
}

