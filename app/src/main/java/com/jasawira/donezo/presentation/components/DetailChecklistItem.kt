package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * DetailChecklistItem
 * Checklist item untuk detail screen dengan dukungan tampilan Info (Deadline & Waktu)
 */
@Composable
fun DetailChecklistItem(
    modifier: Modifier = Modifier,
    itemName: String = "Task name",
    isChecked: Boolean = false,
    isSelected: Boolean = false,
    deadline: LocalDate? = null,
    time: LocalTime? = null,
    hasReminder: Boolean = false,
    onCheckChange: () -> Unit = {},
    backgroundColor: Color = Color.White
) {
    val checkboxColor = Color(0xFF26D3C8)

    val itemBackgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        else -> backgroundColor
    }

    val contentAlpha = if (isChecked) 0.5f else 1f
    val textColor = if (isChecked) Color.Gray else Color.Black

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = itemBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: Checkbox + Text + Info Badges
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (isChecked) checkboxColor else Color.Transparent
                        )
                        .clickable { onCheckChange() }, // Hanya area ini yang merespon klik centang
                    contentAlignment = Alignment.Center
                ) {
                    if (isChecked) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Checked",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        // Empty circle border
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                                .then(Modifier.padding(2.dp))
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = BorderStroke(
                                    2.dp,
                                    Color.Gray.copy(alpha = 0.3f)
                                )
                            ) {}
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Item Detail Column (Name & Info Badges)
                Column {
                    Text(
                        text = itemName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor.copy(alpha = contentAlpha),
                        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                        fontWeight = FontWeight.Medium
                    )

                    // Menampilkan Badge Deadline dan Waktu jika ada
                    if (deadline != null || time != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (deadline != null) {
                                InfoBadge(
                                    icon = Icons.Outlined.CalendarToday,
                                    text = deadline.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                                    color = textColor.copy(alpha = contentAlpha * 0.7f)
                                )
                            }
                            if (time != null) {
                                InfoBadge(
                                    icon = Icons.Outlined.Schedule,
                                    text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    color = textColor.copy(alpha = contentAlpha * 0.7f),
                                    hasReminder = hasReminder
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right side: Status or Drag handle
            if (isChecked) {
                Text(
                    text = "SELESAI ✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = checkboxColor,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = "Drag to reorder",
                    tint = Color.Gray.copy(alpha = 0.3f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoBadge(
    icon: ImageVector,
    text: String,
    color: Color,
    hasReminder: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
        if (hasReminder) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Outlined.NotificationsActive,
                contentDescription = "Reminder aktif",
                tint = color,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DetailChecklistItemPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        DetailChecklistItem(
            itemName = "Meeting bersama tim UI/UX",
            isChecked = false,
            isSelected = false,
            deadline = LocalDate.now(),
            time = LocalTime.of(14, 30),
            hasReminder = true
        )
        DetailChecklistItem(
            itemName = "Buat UI untuk aplikasi",
            isChecked = true,
            isSelected = false,
            deadline = LocalDate.now().plusDays(1)
        )
        DetailChecklistItem(
            itemName = "Review kode",
            isChecked = false,
            isSelected = true
        )
    }
}