package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// ========== EMPTY STATE COMPONENT ==========

/**
 * EmptyStateComponent
 * Ditampilkan saat list kosong
 */
@Composable
fun EmptyStateComponent(
    icon: String = "📭",
    title: String = "Belum ada data",
    subtitle: String = "Mulai dengan membuat yang baru",
    onCreateClick: () -> Unit = {},
    showButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (showButton) {
            Button(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(40.dp)
            ) {
                Text("Buat Sekarang")
            }
        }
    }
}