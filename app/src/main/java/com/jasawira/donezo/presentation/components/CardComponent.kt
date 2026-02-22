package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jasawira.donezo.presentation.theme.ColorPresets

/**
 * CardComponent - Menampilkan single card dengan preview items dan progress bar
 */
@Composable
fun CardComponent(
    cardName: String,
    categoryName: String = "",
    colorPresetId: Int,
    progress: Float = 0f,
    itemCount: Int = 0,
    completedCount: Int = 0,
    onCardClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colorPreset = ColorPresets.getPresetById(colorPresetId)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorPreset.backgroundColor)
            .clickable(onClick = onCardClick)
            .padding(16.dp)
    ) {
        Text(
            text = cardName,
            style = MaterialTheme.typography.titleMedium,
            color = colorPreset.textColor
        )

        if (categoryName.isNotEmpty()) {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.labelSmall,
                color = colorPreset.accentColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                color = colorPreset.primaryColor
            )
            Text(
                text = "$completedCount/$itemCount",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
