package com.jasawira.donezo.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * AddItemBottomSheet (Unified Add & Edit Form)
 * Bottom sheet premium untuk menambah task baru ATAU mengedit task lama.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemBottomSheet(
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    initialName: String = "",
    initialDate: LocalDate? = null,
    initialTime: LocalTime? = null,
    initialReminderMinutes: Int? = null,
    onDismiss: () -> Unit = {},
    onSave: (
        itemName: String,
        deadlineDate: LocalDate?,
        deadlineTime: LocalTime?,
        reminderMinutesBefore: Int?
    ) -> Unit = { _, _, _, _ -> },
    buttonColor: Color = Color(0xFF26D3C8)
) {
    var itemName by remember { mutableStateOf(initialName) }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var selectedTime by remember { mutableStateOf(initialTime) }
    var selectedReminderMinutes by remember { mutableStateOf(initialReminderMinutes) }

    // Date Picker State (Gunakan UTC agar tanggal tidak bergeser karena Timezone lokal)
    var showDatePicker by remember { mutableStateOf(false) }
    val initialDateMillis = remember(initialDate) {
        initialDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    // Time Picker State
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime?.hour ?: LocalTime.now().hour,
        initialMinute = initialTime?.minute ?: LocalTime.now().minute
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditMode) "Edit Task" else "Tambah Task Baru",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", modifier = Modifier.size(18.dp))
                }
            }

            // INPUT NAMA TASK
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                placeholder = { Text("Apa yang ingin kamu selesaikan?", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = buttonColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    cursorColor = buttonColor
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )

            // DEADLINE & WAKTU (Premium Selection Boxes)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Tenggat Waktu",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Date Selector Box
                    SelectionBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.CalendarToday,
                        label = "Tanggal",
                        value = selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                        onClick = { showDatePicker = true },
                        onClear = {
                            selectedDate = null
                            selectedTime = null
                            selectedReminderMinutes = null
                        }
                    )

                    // Time Selector Box (Hanya aktif jika tanggal sudah dipilih)
                    SelectionBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.AccessTime,
                        label = "Waktu",
                        value = selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
                        onClick = { showTimePicker = true },
                        onClear = { selectedTime = null },
                        enabled = selectedDate != null
                    )
                }
            }

            // PENGINGAT (Muncul dengan animasi jika Tanggal sudah dipilih)
            AnimatedVisibility(
                visible = selectedDate != null,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Pengingat",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            null to "Tidak",
                            15 to "15 mnt",
                            30 to "30 mnt",
                            60 to "1 jam"
                        ).forEach { (minutes, label) ->
                            FilterChip(
                                selected = selectedReminderMinutes == minutes,
                                onClick = { selectedReminderMinutes = minutes },
                                label = {
                                    Text(
                                        text = label,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // TOMBOL SIMPAN UTAMA
            Button(
                onClick = {
                    if (itemName.isNotBlank()) {
                        onSave(itemName, selectedDate, selectedTime, selectedReminderMinutes)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(16.dp),
                enabled = itemName.isNotBlank()
            ) {
                Text(
                    text = if (isEditMode) "Simpan Perubahan" else "Tambah Task",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // DIALOG DATE PICKER
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // DIALOG TIME PICKER
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Pilih Jam") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Batal") }
            }
        )
    }
}

/**
 * Komponen reusable untuk Kotak Pemilihan Tanggal & Waktu yang Elegan
 */
@Composable
private fun SelectionBox(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String?,
    onClick: () -> Unit,
    onClear: () -> Unit,
    enabled: Boolean = true
) {
    val backgroundColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    val contentColor = if (enabled) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = contentColor.copy(alpha = 0.7f))
                Text(label, style = MaterialTheme.typography.labelMedium, color = contentColor.copy(alpha = 0.7f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value ?: "Tidak Diatur",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (value != null) FontWeight.SemiBold else FontWeight.Normal,
                    color = contentColor
                )
                if (value != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            .clickable { onClear() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Hapus",
                            modifier = Modifier.size(14.dp),
                            tint = contentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddItemBottomSheetPreview() {
    // Preview Mode Edit
    ItemBottomSheet(
        isEditMode = true,
        initialName = "Meeting dengan klien",
        initialDate = LocalDate.now(),
        initialTime = LocalTime.of(10, 30),
        initialReminderMinutes = 15
    )
}