package com.jasawira.donezo.presentation.components
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jasawira.donezo.domain.model.ChecklistItem
import com.jasawira.donezo.presentation.theme.ColorPreset
import com.jasawira.donezo.presentation.theme.ColorPresets

// ========== CARD COMPONENT ==========

/**
 * CardComponent
 * Menampilkan single card dengan preview 3 items dan progress bar
 */
@Composable
fun CardComponent(
    modifier: Modifier = Modifier,
    cardName: String,
    categoryName: String = "",
    colorPresetId: Int,
    progress: Float = 0f,
    itemCount: Int = 0,
    completedCount: Int = 0,
    previewItems: List<ChecklistItem> = emptyList(),
    onCardClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    isDarkMode: Boolean = false,
) {
    val colorPreset = ColorPresets.getPresetById(colorPresetId)
    val progressPercentage = (progress * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCardClick
            )
            .border(
                width = 1.dp,
                color = colorPreset.borderColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = colorPreset.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Title + Menu
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = cardName,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorPreset.textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (categoryName.isNotEmpty()) {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorPreset.textColor.copy(alpha = 0.7f)
                        )
                    }
                }

                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = colorPreset.accentColor
                    )
                }
            }

            // Progress Bar dengan percentage
            ProgressBarWithPercentage(
                progress = progress,
                progressPercentage = progressPercentage,
                color = colorPreset.primaryColor,
                textColor = colorPreset.textColor
            )

            // Preview Items (max 3)
            PreviewItemsList(
                items = previewItems.take(3),
                colorPreset = colorPreset
            )

            // "Lihat lebih banyak" label
            if (itemCount > 3) {
                Text(
                    text = "+${itemCount - 3} lagi",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorPreset.accentColor,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

// ========== PROGRESS BAR COMPONENT ==========

/**
 * ProgressBarWithPercentage
 * Custom progress bar dengan display percentage
 */
@Composable
fun ProgressBarWithPercentage(
    progress: Float = 0f,
    progressPercentage: Int = 0,
    color: Color = Color(0xFFFFB3D9),
    textColor: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Progress bar dengan animated value
        val animatedProgress by animateFloatAsState(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 300)
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
        )

        // Percentage text
        Text(
            text = "$progressPercentage%",
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

// ========== CHECKLIST ITEM COMPONENT ==========

/**
 * ChecklistItemComponent
 * Menampilkan single checklist item dengan checkbox
 */
@Composable
fun ChecklistItemComponent(
    modifier: Modifier = Modifier,
    itemName: String,
    isChecked: Boolean = false,
    deadline: String? = null,
    hasNotification: Boolean = false,
    onCheckChanged: (Boolean) -> Unit = {},
    onDelete: () -> Unit = {},
    colorPreset: ColorPreset = ColorPresets.pastelBlue,
    isDragging: Boolean = false,
) {
    val itemScale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200)
    )

    val itemColor by animateColorAsState(
        targetValue = if (isChecked) {
            colorPreset.primaryColor.copy(alpha = 0.3f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 300)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(itemScale)
            .background(
                color = itemColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Checkbox dengan animation
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onCheckChanged(it) },
            colors = CheckboxDefaults.colors(
                checkedColor = colorPreset.primaryColor,
                uncheckedColor = colorPreset.borderColor
            ),
            modifier = Modifier.size(24.dp)
        )

        // Item name + deadline
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = itemName,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isChecked) colorPreset.textColor.copy(alpha = 0.5f) else colorPreset.textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = if (isChecked) Modifier else Modifier
            )

            if (!deadline.isNullOrEmpty()) {
                Text(
                    text = deadline,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorPreset.textColor.copy(alpha = 0.6f)
                )
            }
        }

        // Icons: Notification + Delete
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (hasNotification) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Notifikasi aktif",
                    tint = colorPreset.accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = Color.Red.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ========== PREVIEW ITEMS LIST ==========

/**
 * PreviewItemsList
 * Menampilkan preview 3 items dalam card
 */
@Composable
fun PreviewItemsList(
    items: List<ChecklistItem>,
    colorPreset: ColorPreset,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Checkbox visual
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(1.dp, colorPreset.borderColor, RoundedCornerShape(2.dp))
                        .background(
                            color = if (item.isChecked) colorPreset.primaryColor else Color.Transparent,
                            shape = RoundedCornerShape(2.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isChecked) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }

                // Item name
                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorPreset.textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ========== COLOR PRESET PICKER ==========

/**
 * ColorPresetPicker
 * Grid selector untuk memilih color preset
 */
@Composable
fun ColorPresetPicker(
    modifier: Modifier = Modifier,
    selectedColorId: Int = 0,
    onColorSelected: (Int) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Pilih Warna",
            style = MaterialTheme.typography.titleMedium
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ColorPresets.allPresets) { preset ->
                ColorPresetItem(
                    preset = preset,
                    isSelected = preset.id == selectedColorId,
                    onClick = { onColorSelected(preset.id) }
                )
            }
        }
    }
}

/**
 * ColorPresetItem
 * Single color item dalam picker
 */
@Composable
fun ColorPresetItem(
    preset: ColorPreset,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(preset.backgroundColor)
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = preset.primaryColor,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = preset.primaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Text(
            text = preset.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(60.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrevCard(){
    CardComponent(
        cardName = "Sample Card",
        colorPresetId = 6,
        progress = 0.9f,
        itemCount = 10,
        completedCount = 9
    )
}
