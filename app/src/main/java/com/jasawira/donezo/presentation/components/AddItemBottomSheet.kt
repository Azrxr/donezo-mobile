package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jasawira.donezo.domain.model.Category
import java.time.LocalDate
import java.time.LocalTime

/**
 * AddItemBottomSheet
 * Bottom sheet untuk tambah item baru dengan deadline dan notifikasi
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemBottomSheet(
    modifier: Modifier = Modifier,
    categories: List<Category> = emptyList(),
    onDismiss: () -> Unit = {},
    onAddItem: (
        itemName: String,
        deadline: LocalDate?,
        notificationTime: LocalTime?,
        isNotificationEnabled: Boolean,
        notificationMinutesBefore: Int,
        categoryId: String?
    ) -> Unit = { _, _, _, _, _, _ -> },
    buttonColor: Color = Color(0xFF26D3C8)
) {
    var itemName by remember { mutableStateOf("") }
    var selectedDeadline by remember { mutableStateOf<LocalDate?>(null) }
    var selectedNotificationTime by remember { mutableStateOf<LocalTime?>(null) }
    var isNotificationEnabled by remember { mutableStateOf(false) }
    var notificationMinutesBefore by remember { mutableStateOf(30) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var newCategoryName by remember { mutableStateOf("") }
    var showAddCategoryField by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tambah Item Baru",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            HorizontalDivider()

            // Item Name Input
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Nama Item") },
                placeholder = { Text("Masukkan nama item...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category Selection
            Text(
                text = "Kategori (Opsional)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

            if (!showAddCategoryField) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Existing categories
                    categories.forEach { category ->
                        FilterChipItem(
                            label = category.name,
                            isSelected = selectedCategoryId == category.id,
                            onClick = { selectedCategoryId = category.id }
                        )
                    }

                    // Add new category button
                    Button(
                        onClick = { showAddCategoryField = true },
                        modifier = Modifier.height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kategori Baru")
                    }
                }
            } else {
                // Add new category input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Nama Kategori Baru") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            showAddCategoryField = false
                            newCategoryName = ""
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Batal")
                    }
                }
            }

            // Deadline Section
            Text(
                text = "Deadline (Opsional)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

            // Deadline Date Picker
            OutlinedButton(
                onClick = { /* TODO: Show date picker */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (selectedDeadline != null) {
                        "Tanggal: $selectedDeadline"
                    } else {
                        "Pilih Tanggal Deadline"
                    }
                )
            }

            // Notification Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Aktifkan Notifikasi",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                Switch(
                    checked = isNotificationEnabled,
                    onCheckedChange = { isNotificationEnabled = it }
                )
            }

            // Notification Time (only show if enabled)
            if (isNotificationEnabled) {
                OutlinedButton(
                    onClick = { /* TODO: Show time picker */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedNotificationTime != null) {
                            "Waktu: $selectedNotificationTime"
                        } else {
                            "Pilih Waktu Notifikasi"
                        }
                    )
                }

                // Minutes Before Reminder
                Text(
                    text = "Pengingat Sebelumnya (Menit)",
                    style = MaterialTheme.typography.labelSmall
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { index ->
                        val minutes = listOf(15, 30, 45)[index]
                        FilterChipItem(
                            label = "$minutes min",
                            isSelected = notificationMinutesBefore == minutes,
                            onClick = { notificationMinutesBefore = minutes }
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Batal")
                }

                Button(
                    onClick = {
                        if (itemName.isNotBlank()) {
                            onAddItem(
                                itemName,
                                selectedDeadline,
                                selectedNotificationTime,
                                isNotificationEnabled,
                                notificationMinutesBefore,
                                selectedCategoryId
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text("Tambah Item", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


