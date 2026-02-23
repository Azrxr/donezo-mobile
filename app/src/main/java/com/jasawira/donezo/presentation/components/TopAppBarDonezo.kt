package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jasawira.donezo.R
import com.jasawira.donezo.presentation.theme.Spacing

/**
 * TopAppBarDonezo
 * Custom top app bar dengan branding Donezo + search + settings
 */
@Composable
fun TopAppBarDonezo(
    modifier: Modifier = Modifier,
    searchValue: String = "",
    onSearchChange: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    backgroundColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                spotColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.1f)
            )
            .padding(horizontal = Spacing.lg, vertical = Spacing.md)
    ) {
        // Header: Logo + Brand + Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo + Brand Name
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo Icon
                Image(
                    painter = painterResource(id = R.drawable.ic_brand_nobg),
                    contentDescription = "Donezo Logo",
                    modifier = Modifier.size(32.dp)
                )

                // Brand Name
                Text(
                    text = "Donezo",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Settings Button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        // Search Bar
        SearchBarComponent(
            searchQuery = searchValue,
            onSearchChange = onSearchChange,
            placeholder = "Cari kartu atau item..."
        )
    }
}

