package com.jasawira.donezo.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * ReorderableCardWrapper
 * Wraps CompactCardComponent dengan long press gesture untuk reorder
 * Menampilkan shake animation saat long press
 */
@Composable
fun ReorderableCardWrapper(
    modifier: Modifier = Modifier,
    onLongPress: () -> Unit = {},
    onTap: () -> Unit = {},
    content: @Composable (Boolean) -> Unit
) {
    var isLongPressed = remember { mutableStateOf(false) }
    val shakeAnimation = remember { Animatable(0f) }

    LaunchedEffect(isLongPressed.value) {
        if (isLongPressed.value) {
            // Start shake animation - 6 times shake left and right
            repeat(6) { index ->
                shakeAnimation.animateTo(
                    if (index % 2 == 0) 8f else -8f,
                    animationSpec = tween(50, easing = LinearEasing)
                )
            }
            shakeAnimation.animateTo(0f, animationSpec = tween(50))
        }
    }

    Box(
        modifier = modifier
            .offset(x = shakeAnimation.value.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { _ ->
                        val pressDuration = System.currentTimeMillis()
                        tryAwaitRelease()
                        val releaseDuration = System.currentTimeMillis()
                        val isLongPressEvent = (releaseDuration - pressDuration) > 500

                        if (isLongPressEvent) {
                            isLongPressed.value = true
                            onLongPress()
                            delay(300)
                            isLongPressed.value = false
                        } else {
                            onTap()
                        }
                    }
                )
            }
    ) {
        content(isLongPressed.value)
    }
}

