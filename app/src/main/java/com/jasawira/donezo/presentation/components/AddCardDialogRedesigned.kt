package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.presentation.theme.ColorPresets
import com.jasawira.donezo.presentation.theme.Spacing
import java.util.*

/**
 * AddCardDialogRedesigned
 * Dialog redesign untuk tambah card baru dengan UX yang lebih baik
 * - Input nama card
 * - Pilih kategori (dengan opsi tambah baru)
 * - Pilih color preset (horizontal scroll)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardDialogRedesigned(
    modifier: Modifier = Modifier,
    categories: List<Category> = emptyList(),
    onDismiss: () -> Unit = {},
    onAddCard: (name: String, categoryId: String, colorPresetId: Int) -> Unit = { _, _, _ -> },
    onAddCategory: (name: String) -> Unit = {},
    buttonColor: Color = Color(0xFF26D3C8)
) {
    var cardName by remember { mutableStateOf("") }

    // Get Uncategorized category ID from list, atau gunakan placeholder
    val uncategorizedCategory = categories.find { it.name.equals("Uncategorized", ignoreCase = true) }
    val defaultCategoryId = uncategorizedCategory?.id ?: "uncategorized_default"

    var selectedCategoryId by remember { mutableStateOf(defaultCategoryId) }
    var selectedColorPresetId by remember { mutableStateOf(kotlin.random.Random.nextInt(0, 10)) }
    var newCategoryName by remember { mutableStateOf("") }
    var showAddCategoryField by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // Header dengan close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Buat Card Baru",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            // Input Nama Card (PROMINENT)
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = "Nama Card",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = cardName,
                    onValueChange = { cardName = it },
                    placeholder = { Text("Masukkan nama card...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }

            // Category Selection
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = "Pilih Kategori",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                if (!showAddCategoryField) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        // Existing categories from database
                        categories.forEach { category ->
                            FilterChipItem(
                                label = category.name,
                                isSelected = selectedCategoryId == category.id,
                                onClick = { selectedCategoryId = category.id }
                            )
                        }

                        // Add new category button
                        Button(
                            onClick = { showAddCategoryField = true },
                            modifier = Modifier.height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text("Kategori Baru", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                } else {
                    // Add new category input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            placeholder = { Text("Nama kategori baru...") },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            singleLine = true
                        )
                        // Save new category
                        IconButton(
                            onClick = {
                                if (newCategoryName.isNotBlank()) {
                                    onAddCategory(newCategoryName)
                                    // Set the new category as selected (will be updated after category is saved)
                                    newCategoryName = ""
                                    showAddCategoryField = false
                                }
                            }
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Simpan", tint = buttonColor)
                        }
                        // Cancel
                        IconButton(
                            onClick = {
                                showAddCategoryField = false
                                newCategoryName = ""
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Batal")
                        }
                    }
                }
            }

            // Color Preset Selection (Horizontal Scroll)
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = "Pilih Warna",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ColorPresets.allPresets.forEach { preset ->
                        ColorPresetItem(
                            colorPreset = preset,
                            isSelected = selectedColorPresetId == preset.id,
                            onClick = { selectedColorPresetId = preset.id }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Action Buttons (PROMINENT CTA)
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Button "Buat Card" (BOLD, PRIMARY)
                Button(
                    onClick = {
                        if (cardName.isNotBlank()) {
                            onAddCard(cardName, selectedCategoryId, selectedColorPresetId)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = MaterialTheme.shapes.medium,
                    enabled = cardName.isNotBlank()
                ) {
                    Text(
                        "✓ Buat Card",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Button "Batal" (SUBTLE, SECONDARY)
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        "Batal",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

/**
 * Color Preset Item - Circular color selector
 */
@Composable
fun ColorPresetItem(
    colorPreset: com.jasawira.donezo.presentation.theme.ColorPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(colorPreset.backgroundColor)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) colorPreset.primaryColor else Color.Gray.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = colorPreset.textColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AddCardDialogRedesignedPreview() {
    val now = java.time.LocalDateTime.now()
    val dummyCategories = listOf(
        Category(id = "1", name = "Kerja", createdAt = now),
        Category(id = "2", name = "Pribadi", createdAt = now),
        Category(id = "3", name = "Belanja", createdAt = now)
    )
    // For preview, we can't show ModalBottomSheet directly
    // This is just to show the components work
}


