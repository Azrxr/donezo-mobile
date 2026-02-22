package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ========== NOTIFICATION TIME INPUT ==========

/**
 * NotificationTimeInputSpinner
 * Spinner untuk memilih notifi menit sebelum deadline
 */
@Composable
fun NotificationTimeInputSpinner(
    selectedMinutes: Int = 30,
    onMinutesSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
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