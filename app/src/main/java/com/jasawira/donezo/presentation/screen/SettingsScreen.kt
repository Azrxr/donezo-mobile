package com.jasawira.donezo.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jasawira.donezo.presentation.theme.Spacing
import com.jasawira.donezo.presentation.utils.UserPreferencesManager
import com.jasawira.donezo.presentation.utils.AppVersionUtils
/**
 * SettingsScreen
 * Halaman settings untuk mengatur theme
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onThemeChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferencesManager(context) }

    var selectedTheme by remember { mutableStateOf(userPreferences.getThemeMode()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
            contentPadding = PaddingValues(Spacing.xl)
        ) {
            // Theme Section
            item {
                SettingsSectionTheme(
                    selectedTheme = selectedTheme,
                    onThemeChange = { theme ->
                        selectedTheme = theme
                        userPreferences.setThemeMode(theme)
                        onThemeChange(theme)
                    }
                )
            }

            // About Section
            item {
                SettingsSectionAbout()
            }
        }
    }
}

/**
 * Settings Section: Theme (Light/Dark/System)
 */
@Composable
fun SettingsSectionTheme(
    modifier: Modifier = Modifier,
    selectedTheme: String = "system",
    onThemeChange: (String) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Header
        Text(
            text = "Tema Aplikasi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Theme Options
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            listOf("light", "dark", "system").forEach { theme ->
                ThemeOption(
                    label = when (theme) {
                        "light" -> "Mode Terang"
                        "dark" -> "Mode Gelap"
                        "system" -> "Ikuti Sistem"
                        else -> theme
                    },
                    isSelected = selectedTheme == theme,
                    onClick = { onThemeChange(theme) }
                )
            }
        }
    }
}

/**
 * Theme Option Row
 */
@Composable
fun ThemeOption(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}


/**
 * Settings Section: About
 */
@Composable
fun SettingsSectionAbout(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Text(
            text = "Tentang Aplikasi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                AboutItem(label = "Nama Aplikasi", value = "Donezo")
                AboutItem(label = "Versi", value = "${AppVersionUtils.getVersionName()}")
            }
        }
    }
}

/**
 * About Item Row
 */
@Composable
fun AboutItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}



