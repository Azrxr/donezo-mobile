package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.presentation.theme.Spacing
import java.util.*

/**
 * AddCardDialogRedesigned
 * Dialog redesign untuk tambah card baru dengan UX yang lebih baik
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardDialogRedesigned(
    modifier: Modifier = Modifier,
    categories: List<Category> = emptyList(),
    onDismiss: () -> Unit = {},
    onAddCard: (name: String, categoryId: String) -> Unit = { _, _ -> },
    buttonColor: Color = Color(0xFF26D3C8)
) {
    var cardName by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("uncategory") }
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
                        // Uncategory chip
                        FilterChipItem(
                            label = "Uncategory",
                            isSelected = selectedCategoryId == "uncategory",
                            onClick = { selectedCategoryId = "uncategory" }
                        )

                        // Existing categories
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
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            label = { Text("Nama Kategori Baru") },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            singleLine = true
                        )
                        IconButton(
                            onClick = {
                                showAddCategoryField = false
                                newCategoryName = ""
                            },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Batal")
                        }
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
                            onAddCard(cardName, selectedCategoryId)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = MaterialTheme.shapes.medium
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


