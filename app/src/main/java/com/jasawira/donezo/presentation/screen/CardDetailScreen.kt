package com.jasawira.donezo.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jasawira.donezo.presentation.viewmodel.CardDetailViewModel

/**
 * CardDetailScreen
 * Menampilkan detail card dengan checklist items
 */
@Composable
fun CardDetailScreen(
    cardId: String,
    onBack: () -> Unit,
    viewModel: CardDetailViewModel = hiltViewModel()
) {
    // Load card with items
    androidx.compose.runtime.LaunchedEffect(cardId) {
        viewModel.loadCardWithItems(cardId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Card Detail Screen - $cardId")
        // TODO: Implement UI
    }
}

