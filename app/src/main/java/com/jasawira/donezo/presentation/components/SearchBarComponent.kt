package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * SearchBarComponent
 * Search bar elegan berbentuk Pill (Kapsul)
 */
@Composable
fun SearchBarComponent(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    onSearchChange: (String) -> Unit = {},
    placeholder: String = "Cari tugas atau langkah..."
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp) // Ukuran standar yang nyaman diketik
            .padding(horizontal = 4.dp), // Beri sedikit nafas
        shape = CircleShape, // Bentuk Pill modern
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.Transparent, // Hilangkan border saat tidak diklik
            focusedContainerColor = MaterialTheme.colorScheme.surface, // Sedikit cerah saat diketik
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), // Redup saat tidak aktif
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
@Preview(showBackground = true)
fun SearchBarComponentPreview() {
    SearchBarComponent()
}