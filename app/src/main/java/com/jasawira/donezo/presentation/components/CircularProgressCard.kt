package com.jasawira.donezo.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jasawira.donezo.presentation.theme.ColorPresets

/**
 * CircularProgressCard
 * Card progress dengan circular indicator dan linear progress bar
 */
@Composable
fun CircularProgressCard(
    modifier: Modifier = Modifier,
    progress: Float = 0f, // 0.0 to 1.0
    colorPresetId: Int = 0,
    completedCount: Int = 0,
    totalCount: Int = 0
) {

    val preset = ColorPresets.getPresetById(colorPresetId)

    val backgroundColor = preset.backgroundColor
    val accentColor = preset.primaryColor
    val textColor = preset.textColor

    val contentColor = contentColorFor(backgroundColor)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Text info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "PROGRES",
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Text(
                    text = when {
                        percentage >= 100 -> "Selamat! Semua selesai! 🎉"
                        percentage >= 75 -> "Hampir selesai! 🎯"
                        percentage >= 50 -> "Terus semangat! 💪"
                        percentage >= 25 -> "Kamu bisa! 🚀"
                        else -> "Mari mulai! ✨"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Linear progress bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = accentColor,
                    trackColor = contentColor.copy(alpha = 0.3f),
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right side: Circular progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                // Circular progress indicator
                Canvas(modifier = Modifier.size(120.dp)) {
                    // Background circle
                    drawCircle(
                        color = contentColor.copy(alpha = 0.3f),
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Progress arc
                    drawArc(
                        color = accentColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Icon in center
                Text(
                    text = "🎯",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CircularProgressCardPreview() {
    CircularProgressCard()
}

