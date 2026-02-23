package com.jasawira.donezo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.jasawira.donezo.presentation.navigation.ChecklistAppNavGraph
import com.jasawira.donezo.presentation.theme.DonezoTheme
import com.jasawira.donezo.presentation.utils.UserPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity
 * Entry point aplikasi Donezo Checklist Manager
 * Handles app initialization, permissions, theme, dan navigation
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted untuk notifications
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission untuk Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val navController = rememberNavController()
            val userPreferences = remember { UserPreferencesManager(this) }

            // Theme state - read from preferences
            var themeMode by remember { mutableStateOf(userPreferences.getThemeMode()) }

            // Determine if dark theme based on themeMode
            val isDarkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme() // "system" - follow system
            }

            DonezoTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChecklistAppNavGraph(
                        navController = navController,
                        onThemeChange = { newTheme ->
                            themeMode = newTheme
                        }
                    )
                }
            }
        }
    }
}
