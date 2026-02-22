package com.jasawira.donezo.presentation.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ========== SKELETON LOADING COMPONENT ==========

/**
 * SkeletonLoadingCard
 * Placeholder saat loading dengan shimmer animation
 */
@Composable
fun SkeletonLoadingCard(
    modifier: Modifier = Modifier
) {
    val shimmer = rememberInfiniteTransition(label = "shimmer")
    val alpha = shimmer.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ),
        label = "alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.Gray.copy(alpha = alpha.value),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {}
    }
}

/**
 * SkeletonLoadingList
 * Multiple skeleton items untuk list loading
 */
@Composable
fun SkeletonLoadingList(
    count: Int = 3,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(count) {
            SkeletonLoadingCard()
        }
    }
}
