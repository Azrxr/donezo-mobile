package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jasawira.donezo.presentation.theme.ColorPresets

/**
 * CompactCardComponent
 * Card design baru sesuai gambar dengan max 3 items visible
 */
@Composable
fun CompactCardComponent(
    modifier: Modifier = Modifier,
    cardName: String = "Card Name",
    colorPresetId: Int = 0,
    completedCount: Int = 0,
    totalCount: Int = 0,
    items: List<ChecklistItemPreview> = emptyList(),
    onCardClick: () -> Unit = {},
    onItemCheckChange: (String, Boolean) -> Unit = { _, _ -> }
) {
    val colorPreset = ColorPresets.getPresetById(colorPresetId)
    val backgroundColor = colorPreset.backgroundColor
    val progressPercentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Progress text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "$completedCount/$totalCount item selesai",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }

            // Card Title
            Text(
                text = cardName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            // Checklist Items (max 3)
            if (items.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.take(3).forEach { item ->
                        ChecklistItemRow(
                            item = item,
                            onCheckChange = { onItemCheckChange(item.id, !item.isChecked) }
                        )
                    }
                }
            } else {
                Text(
                    text = "Tidak ada item",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Progress Bar with Percentage
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progressPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF26D3C8),
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

/**
 * Checklist Item Row dalam card
 */
@Composable
fun ChecklistItemRow(
    item: ChecklistItemPreview,
    onCheckChange: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox icon
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = if (item.isChecked) "Checked" else "Unchecked",
            tint = if (item.isChecked) Color(0xFF26D3C8) else Color.Gray.copy(alpha = 0.3f),
            modifier = Modifier
                .size(20.dp)
                .clickable { onCheckChange() }
        )

        // Item name
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (item.isChecked) Color.Black.copy(alpha = 0.5f) else Color.Black,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}

/**
 * Data class untuk preview checklist item
 */
data class ChecklistItemPreview(
    val id: String,
    val name: String,
    val isChecked: Boolean
)

@Preview(showBackground = true)
@Composable
fun CompactCardPreview(){
    CompactCardComponent()
}
