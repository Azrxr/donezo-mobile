package com.jasawira.donezo.presentation.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ========== EMPTY STATE COMPONENT ==========

// ========== EMPTY STATE COMPONENT ==========

/**
 * EmptyStateComponent
 * Ditampilkan saat list kosong
 */
@Composable
fun EmptyStateComponent(
    modifier: Modifier = Modifier,
    icon: String = "📭",
    title: String = "Belum ada data",
    subtitle: String = "Mulai dengan membuat yang baru",
    onCreateClick: () -> Unit = {},
    showButton: Boolean = true
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

// ========== SKELETON LOADING COMPONENT ==========

/**
 * SkeletonLoadingCard
 * Placeholder saat loading
 */
@Composable
fun SkeletonLoadingCard(
    modifier: Modifier = Modifier
) {
    val shimmer = rememberInfiniteTransition(label = "shimmer")
    val alpha by shimmer.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1000)),
        label = "alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.Gray.copy(alpha = alpha),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {}
    }
}

/**
 * SkeletonLoadingList
 * Multiple skeleton items
 */
@Composable
fun SkeletonLoadingList(
    modifier: Modifier = Modifier,
    count: Int = 3
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(count) {
            SkeletonLoadingCard()
        }
    }
}

// ========== NOTIFICATION TIME INPUT ==========

/**
 * NotificationTimeInputSpinner
 * Spinner untuk memilih notifi menit sebelum deadline
 */
@Composable
fun NotificationTimeInputSpinner(
    modifier: Modifier = Modifier,
    selectedMinutes: Int = 30,
    onMinutesSelected: (Int) -> Unit = {}
) {
    val minuteOptions = listOf(15, 30, 45)
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("$selectedMinutes menit sebelumnya")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            minuteOptions.forEach { minutes ->
                DropdownMenuItem(
                    text = { Text("$minutes menit sebelumnya") },
                    onClick = {
                        onMinutesSelected(minutes)
                        expanded = false
                    }
                )
            }
        }
    }
}