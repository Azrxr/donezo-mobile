package com.jasawira.donezo.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jasawira.donezo.presentation.components.*
import com.jasawira.donezo.presentation.uistate.HomeUiEvent
import com.jasawira.donezo.presentation.uistate.HomeUiState
import com.jasawira.donezo.presentation.uistate.CardPreview
import com.jasawira.donezo.presentation.uistate.SnackbarEvent
import com.jasawira.donezo.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/**
 * HomeScreen - Redesigned
 * Main screen dengan design baru sesuai gambar
 */
@Composable
fun HomeScreenRedesign(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onCardClick: (String) -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddCardSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Listen to snackbar events
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { event ->
            scope.launch {
                when (event) {
                    is SnackbarEvent.Success -> snackbarHostState.showSnackbar(event.message)
                    is SnackbarEvent.Error -> snackbarHostState.showSnackbar(event.message)
                    is SnackbarEvent.Info -> snackbarHostState.showSnackbar(event.message)
                    else -> {}
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCardSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Top App Bar dengan logo, search, dan settings
            item {
                TopAppBarDonezo(
                    searchValue = searchQuery,
                    onSearchChange = { query ->
                        searchQuery = query
                        viewModel.onEvent(HomeUiEvent.SearchCards(query))
                    },
                    onSettingsClick = onSettingsClick
                )
            }

            // Welcome Header
            item {
                WelcomeHeader(
                    userName = userName,
                    onUserNameChange = { newName ->
                        viewModel.updateUserName(newName)
                    }
                )
            }


            // Category Filter Chips
            item {
                when (val state = uiState) {
                    is HomeUiState.Success -> {
                        CategoryFilterChips(
                            categories = state.categories,
                            selectedCategoryId = state.filterOptions.categoryId,
                            onCategorySelected = { categoryId ->
                                if (categoryId == null) {
                                    viewModel.onEvent(HomeUiEvent.ClearFilter)
                                } else {
                                    viewModel.onEvent(HomeUiEvent.FilterByCategory(categoryId))
                                }
                            }
                        )
                    }
                    else -> {}
                }
            }

            // Content based on UI State
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is HomeUiState.Success -> {
                    val cards = state.filteredCards

                    if (cards.isEmpty()) {
                        // Show create card placeholder when empty
                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                            CreateCardPlaceholder(
                                onClick = { showAddCardSheet = true }
                            )
                        }
                    } else {
                        // Show cards
                        items(
                            items = cards,
                            key = { it.id }
                        ) { card ->
                            ReorderableCardWrapper(
                                onLongPress = {
                                    // Show toast atau visual feedback bahwa card bisa di-reorder
                                    // TODO: Implement haptic feedback
                                },
                                onTap = {
                                    onCardClick(card.id)
                                }
                            ) { isLongPressed ->
                                CompactCardComponent(
                                    cardName = card.name,
                                    colorPresetId = card.colorPresetId,
                                    completedCount = card.completedItemCount,
                                    totalCount = card.itemCount,
                                    items = emptyList(), // TODO: Fetch actual items
                                    onCardClick = {
                                        onCardClick(card.id)
                                    },
                                    onItemCheckChange = { itemId, isChecked ->
                                        // TODO: Handle check change
                                    }
                                )
                            }
                        }

                        // Add placeholder at bottom
                        item {
                            CreateCardPlaceholder(
                                onClick = { showAddCardSheet = true }
                            )
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }

                is HomeUiState.Error -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Error: ${state.message}")
                                Button(onClick = { viewModel.onEvent(HomeUiEvent.RefreshCards) }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Card Dialog
    if (showAddCardSheet) {
        AddCardDialogRedesigned(
            categories = (uiState as? HomeUiState.Success)?.categories ?: emptyList(),
            onDismiss = { showAddCardSheet = false },
            onAddCard = { name, categoryId ->
                viewModel.addCard(name, categoryId) // Color auto-random
                showAddCardSheet = false
            }
        )
    }
}

/**
 * AddCardBottomSheetSimple
 * Simplified version without color picker (auto random)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardBottomSheetSimple(
    categories: List<com.jasawira.donezo.domain.model.Category>,
    onDismiss: () -> Unit = {},
    onAddCard: (name: String, categoryId: String) -> Unit = { _, _ -> }
) {
    var cardName by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Buat Card Baru", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = cardName,
                onValueChange = { cardName = it },
                label = { Text("Nama Card") },
                modifier = Modifier.fillMaxWidth()
            )

            if (categories.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = categories.find { it.id == selectedCategoryId }?.name ?: "Pilih Kategori",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategoryId = category.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (cardName.isNotBlank() && selectedCategoryId.isNotBlank()) {
                            onAddCard(cardName, selectedCategoryId)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text("Buat")
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text("Batal")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



