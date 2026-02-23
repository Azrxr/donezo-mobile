package com.jasawira.donezo.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import com.jasawira.donezo.R
import com.jasawira.donezo.presentation.utils.AppVersionUtils
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val versionAlpha = remember { Animatable(0f) }

    LaunchedEffect(true) {
        // Animate logo
        logoAlpha.animateTo(1f, animationSpec = tween(800))
        delay(200)

        // Animate title
        titleAlpha.animateTo(1f, animationSpec = tween(800))
        delay(200)

        // Animate version
        versionAlpha.animateTo(1f, animationSpec = tween(600))

        // Wait and transition
        delay(1200)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_brand_nobg),
                contentDescription = "Donezo Logo",
                modifier = Modifier
                    .size(120.dp)
                    .alpha(logoAlpha.value)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "DONEZO",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.alpha(titleAlpha.value)
            )
        }

        Text(
            text = "v${AppVersionUtils.getVersionName()}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(versionAlpha.value)
        )
    }
}


