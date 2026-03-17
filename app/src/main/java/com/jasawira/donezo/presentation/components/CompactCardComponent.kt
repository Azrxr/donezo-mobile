package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.background
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
 * Card design untuk Home Screen (Mode Read-Only / Preview)
 */
@Composable
fun CompactCardComponent(
    modifier: Modifier = Modifier,
    cardName: String = "Card Name",
    categoryName: String = "Harian",
    colorPresetId: Int = 0,
    completedCount: Int = 0,
    totalCount: Int = 0,
    items: List<ChecklistItemPreview> = emptyList()
    // Parameter onCardClick dan onItemCheckChange DIBUANG
    // karena interaksi sentuhan sepenuhnya di-handle oleh EditableCardWrapper di luar
) {
    val preset = ColorPresets.getPresetById(colorPresetId)

    val backgroundColor = preset.backgroundColor
    val contentColor = contentColorFor(backgroundColor)
    val accentColor = preset.primaryColor
    val textColor = preset.textColor

    val progress = if (totalCount > 0) {
        completedCount.toFloat() / totalCount
    } else 0f

    // Card tidak memiliki clickable lagi di sini agar tidak konflik dengan Wrapper di luar
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // 🔹 HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(
                    name = categoryName,
                    accentColor = accentColor,
                    textColor = textColor
                )

                Text(
                    text = "$completedCount/$totalCount selesai",
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor.copy(alpha = 0.7f)
                )
            }

            // 🔹 TITLE
            Text(
                text = cardName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

            // 🔹 ITEMS (Mode Preview, Murni pajangan tanpa klik)
            if (items.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items.take(3).forEach { item ->
                        ChecklistItemRow(
                            item = item,
                            contentColor = contentColor,
                            accentColor = accentColor
                        )
                    }
                }
            } else {
                Text(
                    text = "Belum ada langkah",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.5f)
                )
            }

            // 🔹 PROGRESS
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = textColor,
                    trackColor = contentColor.copy(alpha = 0.15f)
                )

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    accentColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = accentColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

/**
 * Checklist Item Row dalam card (Murni Preview)
 */
@Composable
fun ChecklistItemRow(
    item: ChecklistItemPreview,
    contentColor: Color,
    accentColor: Color
) {
    Row(
        // PENTING: Modifier .clickable dihapus total dari sini!
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (item.isChecked)
                accentColor
            else
                contentColor.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (item.isChecked)
                contentColor.copy(alpha = 0.5f)
            else
                contentColor,
            textDecoration = if (item.isChecked)
                TextDecoration.LineThrough
            else
                TextDecoration.None
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