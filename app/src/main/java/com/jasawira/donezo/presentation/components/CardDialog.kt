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
import com.jasawira.donezo.presentation.theme.ColorPreset
import com.jasawira.donezo.presentation.theme.ColorPresets

/**
 * CardDialog (Unified Add & Edit)
 * Dialog premium untuk tambah card baru ATAU edit card lama.
 * Terintegrasi dengan pembuatan kategori secara otomatis tanpa tombol tambahan.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDialog(
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    initialName: String = "",
    initialCategoryId: String? = null,
    initialColorPresetId: Int? = null,
    categories: List<Category> = emptyList(),
    onDismiss: () -> Unit = {},
    // Signature onSave diperbarui untuk mendukung kategori baru secara langsung
    onSave: (name: String, categoryId: String?, newCategoryName: String?, colorPresetId: Int) -> Unit = { _, _, _, _ -> },
    buttonColor: Color = Color(0xFF26D3C8)
) {
    var cardName by remember { mutableStateOf(initialName) }

    // Mencari kategori Uncategorized sebagai default jika tidak ada pilihan
    val uncategorizedId = categories.find { it.name.equals("Uncategorized", ignoreCase = true) }?.id
    val fallbackCategoryId = categories.firstOrNull()?.id ?: ""
    var selectedCategoryId by remember { mutableStateOf(initialCategoryId ?: uncategorizedId ?: fallbackCategoryId) }

    var selectedColorPresetId by remember { mutableStateOf(initialColorPresetId ?: kotlin.random.Random.nextInt(0, 10)) }

    var isCreatingNewCategory by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    // Validasi form aktif jika nama terisi, DAN (kategori ada ATAU nama kategori baru diisi)
    val isFormValid = cardName.isNotBlank() &&
            (if (isCreatingNewCategory) newCategoryName.isNotBlank() else selectedCategoryId.isNotBlank())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditMode) "Edit Card Tugas" else "Buat Card Tugas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", modifier = Modifier.size(18.dp))
                }
            }

            // INPUT NAMA CARD
            OutlinedTextField(
                value = cardName,
                onValueChange = { cardName = it },
                placeholder = { Text("Nama card tugas...", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = buttonColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    cursorColor = buttonColor
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                trailingIcon = {
                    if (cardName.isNotEmpty()) {
                        IconButton(onClick = { cardName = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            )

            // CATEGORY SELECTION (PILIH / BUAT BARU)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Kategori",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                if (isCreatingNewCategory) {
                    // Mode Ketik Kategori Baru (Langsung tersimpan saat tekan tombol Buat Tugas)
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        placeholder = { Text("Ketik kategori baru...", style = MaterialTheme.typography.bodyMedium) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = buttonColor),
                        trailingIcon = {
                            IconButton(onClick = {
                                isCreatingNewCategory = false
                                newCategoryName = ""
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Batal", tint = Color.Gray)
                            }
                        }
                    )
                } else {
                    // Mode Pilih Kategori
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        categories.forEach { category ->
                            val isSelected = selectedCategoryId == category.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryId = category.id },
                                label = { Text(category.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = buttonColor.copy(alpha = 0.15f),
                                    selectedLabelColor = buttonColor
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) buttonColor else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Tombol "+ Baru"
                        Surface(
                            onClick = { isCreatingNewCategory = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(16.dp))
                                Text("Baru", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }

            // COLOR PRESET SELECTION
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Warna Tema",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.height(8.dp))

            // TOMBOL SIMPAN UTAMA
            Button(
                onClick = {
                    if (isFormValid) {
                        onSave(cardName, selectedCategoryId, if (isCreatingNewCategory) newCategoryName else null, selectedColorPresetId)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(16.dp),
                enabled = isFormValid
            ) {
                Text(
                    text = if (isEditMode) "Simpan Perubahan" else "Buat Card",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ColorPresetItem(
    colorPreset: ColorPreset,
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