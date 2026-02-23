package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

/**
 * DetailChecklistItem
 * Checklist item untuk detail screen dengan drag handle dan selection
 */
@Composable
fun DetailChecklistItem(
    modifier: Modifier = Modifier,
    itemName: String = "Task name",
    isChecked: Boolean = false,
    isSelected: Boolean = false,
    onCheckChange: () -> Unit = {},
    onItemClick: () -> Unit = {},
    backgroundColor: Color = Color.White
) {
    val checkboxColor = Color(0xFF26D3C8)
    val itemBackgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else -> backgroundColor
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = itemBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: Checkbox + Text
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Checkbox
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (isChecked) checkboxColor else Color.Transparent
                        )
                        .clickable { onCheckChange() },
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
                                .then(
                                    Modifier.padding(2.dp)
                                )
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    Color.Gray.copy(alpha = 0.3f)
                                )
                            ) {}
                        }
                    }
                }

                // Item name
                Text(
                    text = itemName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isChecked) Color.Gray.copy(alpha = 0.5f) else Color.Black,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            // Right side: Status or Drag handle
            if (isChecked) {
                Text(
                    text = "SELESAI ✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = checkboxColor,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
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

