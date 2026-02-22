package com.jasawira.donezo.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jasawira.donezo.presentation.theme.ColorPresets
import com.jasawira.donezo.presentation.theme.Spacing
import com.jasawira.donezo.presentation.viewmodel.HomeViewModel

/**
 * SettingsScreen
 * Halaman settings untuk mengatur theme dan color preset
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    var selectedTheme by remember { mutableStateOf("system") }
    var selectedColorPreset by remember { mutableStateOf(6) }

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
                        // TODO: Implement theme change
                    }
                )
            }

            // Color Preset Section
            item {
                SettingsSectionColorPreset(
                    selectedColorPreset = selectedColorPreset,
                    onColorPresetChange = { colorId ->
                        selectedColorPreset = colorId
                        viewModel.setColorPreset(colorId)
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
 * Settings Section: Color Preset
 */
@Composable
fun SettingsSectionColorPreset(
    modifier: Modifier = Modifier,
    selectedColorPreset: Int = 6,
    onColorPresetChange: (Int) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Header
        Text(
            text = "Warna Tema",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Color Grid (2 columns)
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            ColorPresets.allPresets.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    row.forEach { preset ->
                        ColorPresetCard(
                            preset = preset,
                            isSelected = selectedColorPreset == preset.id,
                            onClick = { onColorPresetChange(preset.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Placeholder untuk keep symmetry jika ada item ganjil
                    if (row.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Color Preset Card
 */
@Composable
fun ColorPresetCard(
    modifier: Modifier = Modifier,
    preset: com.jasawira.donezo.presentation.theme.ColorPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = preset.backgroundColor
        ),
        onClick = onClick,
        border = if (isSelected) {
            BorderStroke(
                3.dp,
                preset.primaryColor
            )
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Color name
            Text(
                text = preset.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = preset.textColor
            )

            // Color dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                // Primary color dot
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = CircleShape,
                    color = preset.primaryColor
                ) {}

                // Secondary/Accent dot
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = CircleShape,
                    color = preset.accentColor
                ) {}
            }

            // Selected indicator
            if (isSelected) {
                Text(
                    text = "✓ Dipilih",
                    style = MaterialTheme.typography.labelSmall,
                    color = preset.primaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
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
                AboutItem(label = "Versi", value = "1.0.0")
                AboutItem(label = "Developer", value = "Jasawira Team")
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



