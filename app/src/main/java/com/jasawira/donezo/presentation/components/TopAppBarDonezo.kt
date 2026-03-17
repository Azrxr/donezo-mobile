package com.jasawira.donezo.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jasawira.donezo.R

import com.jasawira.donezo.presentation.theme.Spacing

/**
 * TopAppBarDonezo
 * Custom top app bar tanpa bayangan kasar, berpadu mulus dengan latar.
 */
@Composable
fun TopAppBarDonezo(
    modifier: Modifier = Modifier,
    searchValue: String = "",
    onSearchChange: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    backgroundColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.background
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 12.dp)
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
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_brand_nobg),
                    contentDescription = "Donezo Logo",
                    modifier = Modifier.size(34.dp)
                )

                Text(
                    text = "Donezo",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Settings Button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar (Pill Shape)
        SearchBarComponent(
            searchQuery = searchValue,
            onSearchChange = onSearchChange,
            placeholder = "Cari tugas atau item..."
        )
    }
}